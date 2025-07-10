package me.anno.audio2d.spatial

class LowPassFilter(var alpha: Float = 0.5f) {
    private var lastSample = 0f

    fun process(sample: Float): Float {
        lastSample += alpha * (sample - lastSample)
        return lastSample
    }
}