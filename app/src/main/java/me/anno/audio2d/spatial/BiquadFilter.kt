package me.anno.audio2d.spatial

import me.anno.audio2d.AudioWorker
import me.anno.maths.Maths.TAUf
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class BiquadFilter {

    private var b0 = 0f
    private var b1 = 0f
    private var b2 = 0f
    private var a1 = 0f
    private var a2 = 0f

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    init {
        // this sample rate must be AudioWorker.sampleRate
        setLowPass(AudioWorker.sampleRate.toFloat(), 5000f)
    }

    fun setLowPass(sampleRate: Float, cutoffHz: Float, invQ: Float = 1.414f) {

        val omega = TAUf * cutoffHz / sampleRate
        val alpha = sin(omega) * 0.5f * invQ
        val cosW = cos(omega)

        val b0 = (1f - cosW) * 0.5f
        val b1 = 1f - cosW
        val b2 = (1f - cosW) * 0.5f
        val a0 = 1f + alpha
        val a1 = -2f * cosW
        val a2 = 1f - alpha

        // Normalize coefficients
        this.b0 = b0 / a0
        this.b1 = b1 / a0
        this.b2 = b2 / a0
        this.a1 = a1 / a0
        this.a2 = a2 / a0
    }

    fun process(sample: Float): Float {
        val result = b0 * sample + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = sample
        y2 = y1
        y1 = result
        return result
    }

    fun clear() {
        x1 = 0f
        x2 = 0f
        y1 = 0f
        y2 = 0f
    }

    fun isClear(minVolume: Float): Boolean {
        return abs(x1) < minVolume && abs(x2) < minVolume &&
                abs(y1) < minVolume && abs(y2) < minVolume
    }
}