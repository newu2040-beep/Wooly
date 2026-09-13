package com.example.audio.dsp

/**
 * Freeverb / Schroeder Algorithmic Reverberator.
 * Uses 8 parallel feedback comb filters and 4 cascaded all-pass filters per channel.
 */
class SchroederReverb(sampleRate: Int = 44100) {

    private class CombFilter(size: Int) {
        val buffer = FloatArray(size)
        var index = 0
        var filterStore = 0.0f

        fun process(input: Float, feedback: Float, damp: Float): Float {
            val output = buffer[index]
            filterStore = (output * (1.0f - damp)) + (filterStore * damp)
            buffer[index] = input + (filterStore * feedback)
            index = (index + 1) % buffer.size
            return output
        }

        fun mute() {
            buffer.fill(0.0f)
            filterStore = 0.0f
        }
    }

    private class AllPassFilter(size: Int) {
        val buffer = FloatArray(size)
        var index = 0

        fun process(input: Float, feedback: Float = 0.5f): Float {
            val bufOut = buffer[index]
            val output = -input + bufOut
            buffer[index] = input + (bufOut * feedback)
            index = (index + 1) % buffer.size
            return output
        }

        fun mute() {
            buffer.fill(0.0f)
        }
    }

    // Delay lengths tuned for 44.1kHz standard acoustic room response
    private val scale = sampleRate.toFloat() / 44100.0f
    private val combLengthsL = intArrayOf(1116, 1188, 1277, 1356, 1422, 1491, 1557, 1617)
    private val combLengthsR = intArrayOf(1139, 1211, 1300, 1379, 1445, 1514, 1580, 1640)
    private val allPassLengthsL = intArrayOf(556, 441, 341, 225)
    private val allPassLengthsR = intArrayOf(579, 464, 364, 248)

    private val combsL = combLengthsL.map { CombFilter((it * scale).toInt().coerceAtLeast(10)) }
    private val combsR = combLengthsR.map { CombFilter((it * scale).toInt().coerceAtLeast(10)) }
    private val allPassL = allPassLengthsL.map { AllPassFilter((it * scale).toInt().coerceAtLeast(10)) }
    private val allPassR = allPassLengthsR.map { AllPassFilter((it * scale).toInt().coerceAtLeast(10)) }

    var wet = 0.0f
    var roomSize = 0.7f
    var damping = 0.3f
    var dry = 1.0f

    fun setParameters(wetAmount: Float, decay: Float, dampAmount: Float = 0.35f) {
        wet = wetAmount.coerceIn(0.0f, 1.0f)
        dry = (1.0f - wet * 0.4f).coerceIn(0.0f, 1.0f)
        roomSize = (decay * 0.28f + 0.7f).coerceIn(0.7f, 0.98f)
        damping = dampAmount.coerceIn(0.05f, 0.8f)
    }

    fun mute() {
        combsL.forEach { it.mute() }
        combsR.forEach { it.mute() }
        allPassL.forEach { it.mute() }
        allPassR.forEach { it.mute() }
    }

    fun process(inputL: Float, inputR: Float): Pair<Float, Float> {
        if (wet <= 0.001f) {
            return Pair(inputL, inputR)
        }

        val monoInput = (inputL + inputR) * 0.015f

        // Sum 8 parallel comb filters
        var outL = 0.0f
        var outR = 0.0f
        for (i in combsL.indices) {
            outL += combsL[i].process(monoInput, roomSize, damping)
            outR += combsR[i].process(monoInput, roomSize, damping)
        }

        // Pass through 4 series all-pass filters
        for (i in allPassL.indices) {
            outL = allPassL[i].process(outL)
            outR = allPassR[i].process(outR)
        }

        val resultL = inputL * dry + outL * wet
        val resultR = inputR * dry + outR * wet

        return Pair(resultL, resultR)
    }
}
