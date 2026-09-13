package com.example.audio.dsp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Standard 2nd-order Direct Form IIR Biquad Filter based on RBJ Audio EQ Cookbook.
 */
class BiquadFilter {

    private var a0 = 1.0f
    private var a1 = 0.0f
    private var a2 = 0.0f
    private var b0 = 1.0f
    private var b1 = 0.0f
    private var b2 = 0.0f

    private var x1 = 0.0f
    private var x2 = 0.0f
    private var y1 = 0.0f
    private var y2 = 0.0f

    fun reset() {
        x1 = 0.0f
        x2 = 0.0f
        y1 = 0.0f
        y2 = 0.0f
    }

    fun setLowPass(cutoffHz: Float, sampleRate: Float, q: Float = 0.707f) {
        val f0 = (cutoffHz / sampleRate).coerceIn(0.001f, 0.499f)
        val omega = (2.0 * PI * f0).toFloat()
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0f * q)

        val b0Raw = (1.0f - cs) / 2.0f
        val b1Raw = 1.0f - cs
        val b2Raw = (1.0f - cs) / 2.0f
        val a0Raw = 1.0f + alpha
        val a1Raw = -2.0f * cs
        val a2Raw = 1.0f - alpha

        setCoefficients(b0Raw, b1Raw, b2Raw, a0Raw, a1Raw, a2Raw)
    }

    fun setHighPass(cutoffHz: Float, sampleRate: Float, q: Float = 0.707f) {
        val f0 = (cutoffHz / sampleRate).coerceIn(0.001f, 0.499f)
        val omega = (2.0 * PI * f0).toFloat()
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0f * q)

        val b0Raw = (1.0f + cs) / 2.0f
        val b1Raw = -(1.0f + cs)
        val b2Raw = (1.0f + cs) / 2.0f
        val a0Raw = 1.0f + alpha
        val a1Raw = -2.0f * cs
        val a2Raw = 1.0f - alpha

        setCoefficients(b0Raw, b1Raw, b2Raw, a0Raw, a1Raw, a2Raw)
    }

    fun setLowShelf(cutoffHz: Float, sampleRate: Float, gainDb: Float, q: Float = 0.707f) {
        val f0 = (cutoffHz / sampleRate).coerceIn(0.001f, 0.499f)
        val aVal = 10.0f.pow(gainDb / 40.0f)
        val omega = (2.0 * PI * f0).toFloat()
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0f * q)
        val sqrtA = sqrt(aVal) * 2.0f * alpha

        val b0Raw = aVal * ((aVal + 1.0f) - (aVal - 1.0f) * cs + sqrtA)
        val b1Raw = 2.0f * aVal * ((aVal - 1.0f) - (aVal + 1.0f) * cs)
        val b2Raw = aVal * ((aVal + 1.0f) - (aVal - 1.0f) * cs - sqrtA)
        val a0Raw = (aVal + 1.0f) + (aVal - 1.0f) * cs + sqrtA
        val a1Raw = -2.0f * ((aVal - 1.0f) + (aVal + 1.0f) * cs)
        val a2Raw = (aVal + 1.0f) + (aVal - 1.0f) * cs - sqrtA

        setCoefficients(b0Raw, b1Raw, b2Raw, a0Raw, a1Raw, a2Raw)
    }

    fun setHighShelf(cutoffHz: Float, sampleRate: Float, gainDb: Float, q: Float = 0.707f) {
        val f0 = (cutoffHz / sampleRate).coerceIn(0.001f, 0.499f)
        val aVal = 10.0f.pow(gainDb / 40.0f)
        val omega = (2.0 * PI * f0).toFloat()
        val sn = sin(omega)
        val cs = cos(omega)
        val alpha = sn / (2.0f * q)
        val sqrtA = sqrt(aVal) * 2.0f * alpha

        val b0Raw = aVal * ((aVal + 1.0f) + (aVal - 1.0f) * cs + sqrtA)
        val b1Raw = -2.0f * aVal * ((aVal - 1.0f) + (aVal + 1.0f) * cs)
        val b2Raw = aVal * ((aVal + 1.0f) + (aVal - 1.0f) * cs - sqrtA)
        val a0Raw = (aVal + 1.0f) - (aVal - 1.0f) * cs + sqrtA
        val a1Raw = 2.0f * ((aVal - 1.0f) - (aVal + 1.0f) * cs)
        val a2Raw = (aVal + 1.0f) - (aVal - 1.0f) * cs - sqrtA

        setCoefficients(b0Raw, b1Raw, b2Raw, a0Raw, a1Raw, a2Raw)
    }

    private fun setCoefficients(b0R: Float, b1R: Float, b2R: Float, a0R: Float, a1R: Float, a2R: Float) {
        val invA0 = if (a0R != 0f) 1.0f / a0R else 1.0f
        b0 = b0R * invA0
        b1 = b1R * invA0
        b2 = b2R * invA0
        a1 = a1R * invA0
        a2 = a2R * invA0
    }

    fun process(sample: Float): Float {
        val y = b0 * sample + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = sample
        y2 = y1
        y1 = y
        return if (y.isNaN() || y.isInfinite()) 0.0f else y
    }
}

class StereoBiquadFilter {
    val left = BiquadFilter()
    val right = BiquadFilter()

    fun reset() {
        left.reset()
        right.reset()
    }

    fun setLowPass(cutoffHz: Float, sampleRate: Float, q: Float = 0.707f) {
        left.setLowPass(cutoffHz, sampleRate, q)
        right.setLowPass(cutoffHz, sampleRate, q)
    }

    fun setHighPass(cutoffHz: Float, sampleRate: Float, q: Float = 0.707f) {
        left.setHighPass(cutoffHz, sampleRate, q)
        right.setHighPass(cutoffHz, sampleRate, q)
    }

    fun setLowShelf(cutoffHz: Float, sampleRate: Float, gainDb: Float, q: Float = 0.707f) {
        left.setLowShelf(cutoffHz, sampleRate, gainDb, q)
        right.setLowShelf(cutoffHz, sampleRate, gainDb, q)
    }

    fun setHighShelf(cutoffHz: Float, sampleRate: Float, gainDb: Float, q: Float = 0.707f) {
        left.setHighShelf(cutoffHz, sampleRate, gainDb, q)
        right.setHighShelf(cutoffHz, sampleRate, gainDb, q)
    }

    fun process(l: Float, r: Float): Pair<Float, Float> {
        return Pair(left.process(l), right.process(r))
    }
}
