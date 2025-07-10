package me.anno.audio2d

import me.anno.Time
import me.anno.audio2d.spatial.BiquadFilter
import me.anno.audio2d.spatial.DistanceModel
import me.anno.maths.Maths.SECONDS_TO_NANOS
import me.anno.maths.Maths.max
import me.anno.maths.Maths.mix
import me.anno.utils.algorithms.ForLoop.forLoopSafely
import org.joml.Vector3f

class Source {

    val minVolume = 1f / 32768f

    val buffers = ArrayList<ShortArray>()
    var stereo = false
    var spacial = false
    var sampleRate = 48000
        private set(value) {
            field = value
            sampleRateNS = calculateSampleRateNs()
        }

    var volume = 1f
    val position = Vector3f()

    var referenceDistance = 1f
    var rolloffFactor = 1f
    var maxDistance = 10f

    var distanceModel = DistanceModel.NO_ATTENUATION

    private var startTimeNanos = 0L
    private var lastVolumeL = 0f
    private var lastVolumeR = 0f
    private var lastFilterN = 0f
    private var sampleRateNS = calculateSampleRateNs()

    private val filterL = BiquadFilter()
    private val filterR = BiquadFilter()

    private fun calculateSampleRateNs(): Long {
        return sampleRate * (1L shl 30) / SECONDS_TO_NANOS
    }

    fun start() {
        startTimeNanos = Time.nanoTime
    }

    fun fill(dst: FloatArray, timeNanos: Long, dstSampleRate: Int, listener: Listener) {

        var time = timeNanos
        val numDstSamples = dst.size.shr(1)
        val dtNanos = SECONDS_TO_NANOS / dstSampleRate // 1/sampleRate

        val sameSampleRate = dstSampleRate == sampleRate
        val volume1L = calculateVolume(0, listener)
        val volume1R = calculateVolume(1, listener)
        val filter1N = calculateFilterN(listener)

        var volumeL = lastVolumeL
        var volumeR = lastVolumeR
        var filterN = lastFilterN

        val deltaVolumeL = (volume1L - volumeL) / numDstSamples
        val deltaVolumeR = (volume1R - volumeR) / numDstSamples
        val deltaFilterN = (filter1N - filterN) / numDstSamples

        if (buffers.isEmpty() || (volumeL < minVolume && volumeR < minVolume &&
                    volume1L < minVolume && volume1R < minVolume)
        ) {

            // discard any buffers that we don't need
            val endTimeNanos = timeNanos + dtNanos * (numDstSamples - 1)
            sample0(endTimeNanos, true, 0)

            // if buffer wasn't empty before,
            //  and we just need to wait a little longer,
            //  softly let the sound die
            if (!filterL.isClear(minVolume) || !filterR.isClear(minVolume)) {
                if (filterN > 0f || filter1N > 0f) {
                    // empty the filters
                    forLoopSafely(dst.size, 2) { i ->

                        dst[i] += filterL.process(0f) * filterN
                        dst[i + 1] += filterR.process(0f) * filterN

                        time += dtNanos
                        volumeL += deltaVolumeL
                        volumeR += deltaVolumeR
                        filterN += deltaFilterN
                    }
                } else {
                    // filter are unused -> just clear them
                    filterL.clear()
                    filterR.clear()
                }
            }
        } else {
            // must be in one loop, so we don't discard buffers too early
            forLoopSafely(dst.size, 2) { i ->

                val rawL = sample0(timeNanos, sameSampleRate, 0)
                val rawR = if (stereo) sample0(timeNanos, sameSampleRate, 1) else rawL

                val attenuatedL = rawL * volumeL
                val attenuatedR = rawR * volumeR

                dst[i] += mix(attenuatedL, filterL.process(attenuatedL), filterN)
                dst[i + 1] += mix(attenuatedR, filterR.process(attenuatedR), filterN)

                time += dtNanos
                volumeL += deltaVolumeL
                volumeR += deltaVolumeR
                filterN += deltaFilterN
            }
        }

        lastVolumeL = volume1L
        lastVolumeR = volume1R
        lastFilterN = filterN

    }

    private fun sample0(timeNanos: Long, sameSampleRate: Boolean, channel: Int): Float {
        val channelI = if (!stereo) 0 else channel
        val numChannels = if (stereo) 2 else 1
        while (true) {

            val indexRaw = (timeNanos - startTimeNanos) * sampleRate
            val index0 = indexRaw shr 30
            val index = index0 * numChannels + channelI
            val buffer0 = buffers.firstOrNull()
                ?: return 0f // missing buffer :/

            val buffer0Size = buffer0.size
            if (index < 0) {
                return 0f // somehow in the past... idk how...
            }

            if (index >= buffer0Size) {
                val numSamples = if (stereo) buffer0Size.shr(1) else buffer0Size
                startTimeNanos += numSamples.toLong().shl(30) / sampleRateNS
                buffers.removeAt(0) // RingBuffer would be better...
                continue
            }

            if (sameSampleRate || index + numChannels >= buffer0Size) {
                // directly sample it
                return buffer0[index.toInt()].toFloat()
            } else {
                // interpolation
                val v0: Int = buffer0[index.toInt()].toInt()
                val v1: Int = buffer0[index.toInt() + numChannels].toInt()
                val mask: Long = (1L shl 30) - 1L
                val fraction = indexRaw and mask
                val vx: Int = v0 + ((v1 - v0) * fraction).shr(30).toInt()
                return vx.toFloat()
            }
        }
    }

    private fun calculateVolume(channel: Int, listener: Listener): Float {
        if (!spacial) return volume

        val listenerPosition =
            if (channel == 1) listener.rightPosition
            else listener.leftPosition

        val listenerDirection =
            if (channel == 1) listener.rightDirection
            else listener.leftDirection

        val direction = position.sub(listenerPosition, Vector3f())
        val distance = direction.length()

        val distanceAttenuation = distanceModel.calculate(distance, this)

        val dot = direction.dot(listenerDirection) / max(direction.length(), 1e-38f)
        val angleAttenuation = dot * 0.5f + 0.5f

        return volume * angleAttenuation * distanceAttenuation
    }

    private fun calculateFilterN(listener: Listener): Float {
        if (!spacial) return 0f // inactive

        val listenerPosition = listener.position
        val listenerDirection = listener.forward

        val direction = position.sub(listenerPosition, Vector3f())
        val dot = direction.dot(listenerDirection) / max(direction.length(), 1e-38f)
        return max(-dot, 0f)
    }
}