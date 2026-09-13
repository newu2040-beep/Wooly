package com.example.audio.dsp

import com.example.data.model.CustomAudioParams
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.tanh

/**
 * High-performance DSP processing pipeline that transforms raw stereo PCM audio.
 */
class AudioProcessor(val sampleRate: Int = 44100) {

    private val reverb = SchroederReverb(sampleRate)
    private val delay = DelayLine(sampleRate)
    private val spatial = Spatial3DEffect(sampleRate)
    private val vinyl = VinylNoiseGenerator(sampleRate)

    private val lowPassFilter = StereoBiquadFilter()
    private val highPassFilter = StereoBiquadFilter()
    private val bassFilter = StereoBiquadFilter()
    private val trebleFilter = StereoBiquadFilter()

    private var currentParams = CustomAudioParams()

    fun configure(params: CustomAudioParams) {
        currentParams = params

        // Configure Reverb
        reverb.setParameters(
            wetAmount = params.reverbWet,
            decay = params.reverbDecay,
            dampAmount = if (params.lowPassHz < 10000f) 0.6f else 0.35f
        )

        // Configure Delay / Echo
        val echoWet = if (params.echoFeedback > 0f) 0.5f else 0.0f
        delay.setDelay(params.delayMs, sampleRate, params.echoFeedback, echoWet)

        // Configure 3D Spatial & Stereo width
        spatial.spatialIntensity = params.spatial3D
        spatial.stereoWidth = params.stereoWidth

        // Configure Vinyl
        vinyl.crackleAmount = params.vinylCrackle

        // Configure Filters
        lowPassFilter.setLowPass(params.lowPassHz, sampleRate.toFloat())
        highPassFilter.setHighPass(params.highPassHz, sampleRate.toFloat())
        bassFilter.setLowShelf(110.0f, sampleRate.toFloat(), params.bassGainDb)
        trebleFilter.setHighShelf(4200.0f, sampleRate.toFloat(), params.trebleGainDb)
    }

    fun reset() {
        reverb.mute()
        delay.reset()
        spatial.reset()
        vinyl.reset()
        lowPassFilter.reset()
        highPassFilter.reset()
        bassFilter.reset()
        trebleFilter.reset()
    }

    /**
     * Process an entire stereo audio buffer (interleaved [L0, R0, L1, R1, ...]).
     * Includes speed/pitch resampling, DSP effects chain, fade-in/out, and soft-knee limiter.
     */
    fun processBuffer(
        input: FloatArray,
        params: CustomAudioParams,
        onProgress: ((Float) -> Unit)? = null
    ): FloatArray {
        configure(params)
        reset()

        val inputStereoFrames = input.size / 2
        val pitchMultiplier = if (params.pitchSemis != 0.0f) {
            2.0.pow(params.pitchSemis.toDouble() / 12.0).toFloat()
        } else 1.0f

        val effectiveSpeed = (params.speed * pitchMultiplier).coerceIn(0.4f, 2.5f)
        val outputStereoFrames = (inputStereoFrames / effectiveSpeed).toInt().coerceAtLeast(1)
        val output = FloatArray(outputStereoFrames * 2)

        val fadeInFrames = (params.fadeInSec * sampleRate).toInt()
        val fadeOutFrames = (params.fadeOutSec * sampleRate).toInt()

        var inPos = 0.0
        val progressStep = (outputStereoFrames / 100).coerceAtLeast(1000)

        for (outFrame in 0 until outputStereoFrames) {
            if (outFrame % progressStep == 0) {
                onProgress?.invoke(outFrame.toFloat() / outputStereoFrames)
            }

            // Resampling interpolation
            val flutter = vinyl.nextFlutterOffset()
            val adjustedInPos = (inPos + flutter).coerceIn(0.0, (inputStereoFrames - 2).toDouble())

            val frameIndex0 = adjustedInPos.toInt()
            val frac = (adjustedInPos - frameIndex0).toFloat()
            val frameIndex1 = (frameIndex0 + 1).coerceAtMost(inputStereoFrames - 1)

            val inL0 = input[frameIndex0 * 2]
            val inR0 = input[frameIndex0 * 2 + 1]
            val inL1 = input[frameIndex1 * 2]
            val inR1 = input[frameIndex1 * 2 + 1]

            var sampleL = inL0 + (inL1 - inL0) * frac
            var sampleR = inR0 + (inR1 - inR0) * frac

            // 1. Bass & Treble EQ
            val (bassL, bassR) = bassFilter.process(sampleL, sampleR)
            val (trebleL, trebleR) = trebleFilter.process(bassL, bassR)
            sampleL = trebleL
            sampleR = trebleR

            // 2. High-Pass and Low-Pass Filters
            val (hpL, hpR) = highPassFilter.process(sampleL, sampleR)
            val (lpL, lpR) = lowPassFilter.process(hpL, hpR)
            sampleL = lpL
            sampleR = lpR

            // 3. Warmth / Tape Saturation
            if (params.warmthSaturation > 0.01f) {
                sampleL = applyWarmthSaturation(sampleL, params.warmthSaturation)
                sampleR = applyWarmthSaturation(sampleR, params.warmthSaturation)
            }

            // 4. Delay / Echo
            val (echoL, echoR) = delay.process(sampleL, sampleR)
            sampleL = echoL
            sampleR = echoR

            // 5. Reverb
            val (revL, revR) = reverb.process(sampleL, sampleR)
            sampleL = revL
            sampleR = revR

            // 6. 3D Spatial & Stereo Width
            val (spL, spR) = spatial.process(sampleL, sampleR)
            sampleL = spL
            sampleR = spR

            // 7. Vinyl noise and surface dust
            if (params.vinylCrackle > 0.01f) {
                val (noiseL, noiseR) = vinyl.nextNoiseSample()
                sampleL += noiseL
                sampleR += noiseR
            }

            // 8. Fade In / Out
            var envelope = 1.0f
            if (fadeInFrames > 0 && outFrame < fadeInFrames) {
                envelope *= (outFrame.toFloat() / fadeInFrames)
            }
            val framesFromEnd = outputStereoFrames - 1 - outFrame
            if (fadeOutFrames > 0 && framesFromEnd < fadeOutFrames) {
                envelope *= (framesFromEnd.toFloat() / fadeOutFrames)
            }

            // 9. Master Volume & Soft Limiter
            sampleL *= params.volume * envelope
            sampleR *= params.volume * envelope

            output[outFrame * 2] = softLimit(sampleL)
            output[outFrame * 2 + 1] = softLimit(sampleR)

            inPos += effectiveSpeed
            if (inPos >= inputStereoFrames - 1) {
                // Decay reverb tail or pad remaining if desired
                break
            }
        }

        onProgress?.invoke(1.0f)
        return output
    }

    private fun applyWarmthSaturation(sample: Float, drive: Float): Float {
        val x = sample * (1.0f + drive * 1.5f)
        return tanh(x)
    }

    private fun softLimit(sample: Float): Float {
        // Soft-knee limiter preventing digital overs (clipping)
        return if (abs(sample) <= 0.85f) {
            sample
        } else {
            val sign = if (sample > 0) 1.0f else -1.0f
            val x = abs(sample)
            sign * (0.85f + 0.15f * tanh((x - 0.85f) / 0.15f))
        }
    }
}
