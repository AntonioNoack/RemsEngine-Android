package me.anno.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.opengl.GLES20.glUniform1i
import android.view.Surface
import me.anno.Time
import me.anno.ecs.Component
import me.anno.ecs.EntityQuery.getComponent
import me.anno.ecs.components.mesh.MeshComponent
import me.anno.ecs.components.mesh.material.Material
import me.anno.ecs.systems.OnUpdate
import me.anno.gpu.shader.GPUShader
import me.anno.image.raw.GPUImage
import me.anno.maths.Maths.MILLIS_TO_NANOS
import me.anno.utils.types.Booleans.hasFlag
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class VideoPlayComponent private constructor(val videoPath: String) : Component(), OnUpdate {

    companion object {

        fun create(context: Context, assetFileName: String): VideoPlayComponent {
            val videoFile = copyVideoToInternalStorage(context, assetFileName)
            val videoPath = videoFile!!.absolutePath
            return VideoPlayComponent(videoPath)
        }

        private fun copyVideoToInternalStorage(context: Context, assetFileName: String): File? {
            val outFile = File(context.filesDir, assetFileName)
            if (outFile.exists()) return outFile

            try {
                context.assets.open(assetFileName).use { `is` ->
                    FileOutputStream(outFile).use { os ->
                        val buffer = ByteArray(4096)
                        var length: Int
                        while ((`is`.read(buffer).also { length = it }) > 0) {
                            os.write(buffer, 0, length)
                        }
                        os.flush()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return outFile
        }

    }

    var hasFrame = false
    var startTime = 0L

    var surfaceTexture: SurfaceTexture? = null
    val texture = SurfaceTexture2D()
    val material = object : Material() {

        private fun findTextureIndex(shader: GPUShader): Int {
            val texName = shader.getUniformLocation("colorTexture")
            val slot = shader.textureNames.size
            if (texName >= 0) {
                glUniform1i(texName, slot)
                return slot
            } else return -1
        }

        override fun bind(shader: GPUShader) {
            super.bind(shader)
            val slot = findTextureIndex(shader)
            if (slot >= 0) {
                surfaceTexture?.updateTexImage()
                texture.bind(slot, texture.filtering, texture.clamping)
            }
        }
    }

    init {
        material.diffuseMap = GPUImage(texture).ref
        material.shader = VideoPlayShader
    }

    override fun onUpdate() {
        creation.value

        // get mesh component
        // replace diffuse texture with this
        val meshComp = getComponent(MeshComponent::class)
        if (hasFrame && meshComp != null && meshComp.materials.isEmpty()) {
            meshComp.materials = listOf(material.ref)
        }
    }

    // todo recreate when session is recreated
    val creation = lazy {
        texture.checkSession()
        val textureId = texture.pointer
        val surfaceTexture = SurfaceTexture(textureId)
        this.surfaceTexture = surfaceTexture
        surfaceTexture.setOnFrameAvailableListener {
            hasFrame = true
        }
        thread(name = "VideoDecoding") {
            while (true) {
                runVideoDecoderThread(surfaceTexture, videoPath)
            }
        }
    }

    private fun selectTrack(extractor: MediaExtractor): Int {
        for (trackIndex in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(trackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime != null && mime.startsWith("video/")) {
                return trackIndex
            }
        }
        return -1
    }

    private fun createDecoder(
        extractor: MediaExtractor, trackIndex: Int,
        surfaceTexture: SurfaceTexture
    ): MediaCodec {
        extractor.selectTrack(trackIndex)
        val format = extractor.getTrackFormat(trackIndex)
        val mime = format.getString(MediaFormat.KEY_MIME)!!

        val decoder = MediaCodec.createDecoderByType(mime)
        val surface = Surface(surfaceTexture)
        decoder.configure(format, surface, null, 0)
        return decoder
    }

    private fun runVideoDecoderThread(surfaceTexture: SurfaceTexture, videoPath: String) {
        val extractor = MediaExtractor()
        extractor.setDataSource(videoPath)

        // Select video track
        val trackIndex = selectTrack(extractor)
        if (trackIndex < 0) return

        val decoder = createDecoder(extractor, trackIndex, surfaceTexture)
        decoder.start()

        startTime = Time.gameTimeN

        var reachedEnd = false
        val info = MediaCodec.BufferInfo()
        val timeoutMicroSeconds = 10_000L

        while (true) {
            if (!reachedEnd) {
                val inIndex = decoder.dequeueInputBuffer(timeoutMicroSeconds)
                if (inIndex >= 0) {
                    val buffer = decoder.getInputBuffer(inIndex)!!
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(
                            inIndex, 0, 0, 0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        reachedEnd = true
                    } else {
                        val presentationTimeUs = extractor.sampleTime
                        decoder.queueInputBuffer(inIndex, 0, sampleSize, presentationTimeUs, 0)
                        extractor.advance()
                    }
                }
            }

            val outIndex = decoder.dequeueOutputBuffer(info, timeoutMicroSeconds)
            if (outIndex >= 0) {


                // Synchronize presentation
                val presentationTimeNanos = info.presentationTimeUs * 1000
                val currentTimeNanos = Time.gameTimeN - startTime

                if (presentationTimeNanos > currentTimeNanos) {
                    try {
                        val dt = presentationTimeNanos - currentTimeNanos
                        Thread.sleep(dt / MILLIS_TO_NANOS, (dt % MILLIS_TO_NANOS).toInt())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }


                decoder.releaseOutputBuffer(outIndex, true)
            }

            if (info.flags.hasFlag(MediaCodec.BUFFER_FLAG_END_OF_STREAM)) break
        }

        decoder.stop()
        decoder.release()
        extractor.release()


    }

}