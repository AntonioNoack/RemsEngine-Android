package me.anno.audio2d.spatial

import me.anno.audio2d.Source
import me.anno.utils.assertions.assertFail
import kotlin.math.pow

enum class DistanceModel(private val clamped: Boolean) {
    NO_ATTENUATION(false),
    INVERSE_DISTANCE(false),
    INVERSE_DISTANCE_CLAMPED(true),
    LINEAR_DISTANCE(false),
    LINEAR_DISTANCE_CLAMPED(true),
    EXPONENT_DISTANCE(false),
    EXPONENT_DISTANCE_CLAMPED(true);

    fun calculate(distance: Float, source: Source): Float {
        if (this == NO_ATTENUATION || source.rolloffFactor <= 0f) return 1f
        val referenceDistance = source.referenceDistance
        val distanceI =
            if (clamped && !(distance > referenceDistance)) referenceDistance
            else distance
        val rolloffFactor = source.rolloffFactor
        return when (this) {
            INVERSE_DISTANCE,
            INVERSE_DISTANCE_CLAMPED -> {
                referenceDistance / (referenceDistance + rolloffFactor * (distanceI - referenceDistance))
            }
            LINEAR_DISTANCE,
            LINEAR_DISTANCE_CLAMPED -> {
                (1f - rolloffFactor * (distanceI - referenceDistance) / (source.maxDistance - referenceDistance))
            }
            EXPONENT_DISTANCE,
            EXPONENT_DISTANCE_CLAMPED -> {
                (distanceI / referenceDistance).pow(-rolloffFactor)
            }
            else -> assertFail("Impossible")
        }
    }
}