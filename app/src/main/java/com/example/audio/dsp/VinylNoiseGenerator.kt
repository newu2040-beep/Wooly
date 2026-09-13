package com.example.audio.dsp

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Vinyl record noise and turntable mechanical emulation.
 * Synthesizes vinyl surface hiss, dust clicks/pops, and analog pitch flutter.
 */
class VinylNoiseGenerator(private val sampleRate: Int = 44100) {

    private var noiseFilterL = 0.0f
    private var noiseFilterR = 0.0f
    private var clickDecayL = 0.0f
    private var clickDecayR = 0.0f
    private var flutterPhase = 0.0f
    private val random = Random(42)

    var crackleAmount = 0.0f // 0.0 to 1.0

    fun reset() {
        noiseFilterL = 0.0f
        noiseFilterR = 0.0f
        clickDecayL = 0.0f
        clickDecayR = 0.0f
        flutterPhase = 0.0f
    }

    /**
     * Returns flutter pitch offset in samples (-1.0 to 1.0) for subtle turntable wow.
     */
    fun nextFlutterOffset(): Float {
        if (crackleAmount <= 0.01f) return 0.0f
        // 33.33 RPM turntable wow is ~0.555 Hz
        val wow = sin(flutterPhase) * (crackleAmount * 0.9f)
        flutterPhase += (2.0 * PI * 0.555 / sampleRate).toFloat()
        if (flutterPhase > 2.0 * PI) flutterPhase -= (2.0 * PI).toFloat()
        return wow
    }

    /**
     * Synthesizes stereo vinyl surface noise and dust clicks to mix into audio.
     */
    fun nextNoiseSample(): Pair<Float, Float> {
        if (crackleAmount <= 0.01f) return Pair(0.0f, 0.0f)

        // Pink/brownian filtered continuous surface hiss
        val whiteL = (random.nextFloat() * 2.0f - 1.0f) * 0.045f * crackleAmount
        val whiteR = (random.nextFloat() * 2.0f - 1.0f) * 0.045f * crackleAmount
        noiseFilterL = (noiseFilterL * 0.85f) + (whiteL * 0.15f)
        noiseFilterR = (noiseFilterR * 0.85f) + (whiteR * 0.15f)

        // Random dust particle clicks / pops
        if (random.nextFloat() < (0.0018f * crackleAmount)) {
            val popMagnitude = (random.nextFloat() * 0.35f + 0.05f) * crackleAmount
            if (random.nextBoolean()) {
                clickDecayL = popMagnitude
            } else {
                clickDecayR = popMagnitude
            }
        }

        val outL = noiseFilterL + clickDecayL
        val outR = noiseFilterR + clickDecayR

        // Exponential decay of dust clicks
        clickDecayL *= 0.82f
        clickDecayR *= 0.82f

        return Pair(outL, outR)
    }
}
