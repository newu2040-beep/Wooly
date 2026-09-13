package com.example.audio.dsp

/**
 * 3D Surround Binaural Haas Effect & Mid-Side Stereo Widener.
 */
class Spatial3DEffect(sampleRate: Int = 44100) {

    // 0.65ms Haas delay buffer creates realistic interaural time difference (ITD)
    private val haasDelaySamples = (sampleRate * 0.00065f).toInt().coerceAtLeast(4)
    private val delayBufferL = FloatArray(haasDelaySamples + 4)
    private val delayBufferR = FloatArray(haasDelaySamples + 4)
    private var writeIndex = 0

    var spatialIntensity = 0.0f  // 0.0 to 1.0
    var stereoWidth = 1.0f       // 0.0 (mono) to 2.0 (ultra wide)

    fun reset() {
        delayBufferL.fill(0.0f)
        delayBufferR.fill(0.0f)
        writeIndex = 0
    }

    fun process(inL: Float, inR: Float): Pair<Float, Float> {
        // 1. Mid-Side matrix widening
        val mid = (inL + inR) * 0.5f
        val side = (inL - inR) * 0.5f * stereoWidth

        val wideL = mid + side
        val wideR = mid - side

        if (spatialIntensity <= 0.01f) {
            return Pair(wideL, wideR)
        }

        // 2. Haas spatial cross-delay
        val readIndex = (writeIndex - haasDelaySamples + delayBufferL.size) % delayBufferL.size
        val delayedL = delayBufferL[readIndex]
        val delayedR = delayBufferR[readIndex]

        delayBufferL[writeIndex] = wideL
        delayBufferR[writeIndex] = wideR
        writeIndex = (writeIndex + 1) % delayBufferL.size

        // Cross-feed with slight phase inversion for 3D surround sound stage
        val out3DL = wideL * (1.0f - spatialIntensity * 0.25f) + (delayedR * spatialIntensity * 0.45f)
        val out3DR = wideR * (1.0f - spatialIntensity * 0.25f) - (delayedL * spatialIntensity * 0.45f)

        return Pair(out3DL, out3DR)
    }
}
