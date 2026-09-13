package com.example.audio.dsp

/**
 * Ping-Pong stereo delay with low-pass filtered feedback.
 */
class DelayLine(sampleRate: Int = 44100, maxDelayMs: Int = 1000) {

    private val maxSamples = (sampleRate * (maxDelayMs / 1000.0f)).toInt() + 10
    private val bufferL = FloatArray(maxSamples)
    private val bufferR = FloatArray(maxSamples)

    private var writePos = 0
    private var delaySamples = 0
    var feedback = 0.0f
    var wetMix = 0.0f

    private var lowpassL = 0.0f
    private var lowpassR = 0.0f

    fun setDelay(delayMs: Int, sampleRate: Int, feedbackAmount: Float, wet: Float) {
        val msClamped = delayMs.coerceIn(0, 1000)
        delaySamples = ((msClamped / 1000.0f) * sampleRate).toInt().coerceIn(0, maxSamples - 1)
        feedback = feedbackAmount.coerceIn(0.0f, 0.85f)
        wetMix = wet.coerceIn(0.0f, 1.0f)
    }

    fun reset() {
        bufferL.fill(0.0f)
        bufferR.fill(0.0f)
        writePos = 0
        lowpassL = 0.0f
        lowpassR = 0.0f
    }

    fun process(inputL: Float, inputR: Float): Pair<Float, Float> {
        if (delaySamples == 0 || wetMix <= 0.001f) {
            return Pair(inputL, inputR)
        }

        var readPos = writePos - delaySamples
        if (readPos < 0) readPos += maxSamples

        val delayedL = bufferL[readPos]
        val delayedR = bufferR[readPos]

        // One-pole lowpass filter on feedback for analog-style tape echo decay
        lowpassL = lowpassL * 0.4f + delayedR * 0.6f // Cross-feedback (Ping-Pong)
        lowpassR = lowpassR * 0.4f + delayedL * 0.6f

        bufferL[writePos] = inputL + lowpassL * feedback
        bufferR[writePos] = inputR + lowpassR * feedback

        writePos = (writePos + 1) % maxSamples

        val outL = inputL + delayedL * wetMix
        val outR = inputR + delayedR * wetMix

        return Pair(outL, outR)
    }
}
