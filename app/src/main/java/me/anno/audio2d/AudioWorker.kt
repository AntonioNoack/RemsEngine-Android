package me.anno.audio2d

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import me.anno.Time
import me.anno.maths.Maths.SECONDS_TO_NANOS
import me.anno.maths.Maths.align
import kotlin.concurrent.thread

object AudioWorker {

    // todo why is OpenAL never called???

    // todo provide an interface like OpenAL,
    //  given a listener, and set of players,
    //  accumulate their audio,
    //  apply 3d effect to those needed (left/right, high-frequency-filter for back),

    // todo raycasts for realistic echo

    private var playTimeNanos = 0L
    fun fillBuffer(buffer: FloatArray) {

        buffer.fill(0f)
        for (i in activeSources.indices) {
            val source = activeSources[i]
            source.fill(buffer, playTimeNanos, sampleRate, listener)
        }

        val dstNumSamples = buffer.size.shr(1)
        playTimeNanos += dstNumSamples * SECONDS_TO_NANOS / sampleRate
    }

    private var bufferSize = -1
    val sampleRate = 48000

    val listener = Listener
    val activeSources = ArrayList<Source>()

    fun startPlaying(shouldContinue: () -> Boolean) {

        val channelConfig = AudioFormat.CHANNEL_OUT_STEREO
        val encoding = AudioFormat.ENCODING_PCM_FLOAT

        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, encoding)

        val frameSize = 4 * 2 // 4 bytes per sample * 2 channels = 8 bytes per frame
        val bufferSizeInBytes = align(minBufferSize, frameSize)
        bufferSize = bufferSizeInBytes / 4

        val audioTrack = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate, channelConfig, encoding, minBufferSize,
                AudioTrack.MODE_STREAM
            )
        } else {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(encoding)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(bufferSizeInBytes)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        }

        // todo pause when no audio is active for a second (?)
        // only start playing when an audio source has been added

        thread(name = "AudioWorker") {
            val buffer = FloatArray(bufferSize)
            var hasBeenStarted = false
            while (shouldContinue()) {

                if (!hasBeenStarted) {
                    if (activeSources.isEmpty()) {
                        Thread.sleep(10)
                        continue
                    }

                    audioTrack.play()
                    playTimeNanos = Time.nanoTime
                    hasBeenStarted = true
                }

                fillBuffer(buffer)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioTrack.write(
                        buffer, 0, buffer.size,
                        AudioTrack.WRITE_BLOCKING // sleeps automatically
                    )
                }
            }
            if (hasBeenStarted) {
                audioTrack.stop()
            } // else doesn't matter
        }
    }
}