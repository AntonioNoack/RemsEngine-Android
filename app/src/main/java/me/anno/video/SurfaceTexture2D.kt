package me.anno.video

import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES20.GL_RGBA
import me.anno.gpu.DepthMode
import me.anno.gpu.GFXState
import me.anno.gpu.texture.Clamping
import me.anno.gpu.texture.Filtering
import me.anno.gpu.texture.ITexture2D
import me.anno.gpu.texture.Texture2D

class SurfaceTexture2D : ITexture2D {

    companion object {

        private const val TARGET = GLES11Ext.GL_TEXTURE_EXTERNAL_OES

        private fun createExternalTexture(): Int {
            val tex = IntArray(1)
            GLES20.glGenTextures(1, tex, 0)
            GLES20.glBindTexture(TARGET, tex[0])
            GLES20.glTexParameteri(TARGET, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(TARGET, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            return tex[0]
        }
    }

    private var session = 0
    var pointer = -1

    override val channels: Int
        get() = 3

    override val clamping: Clamping
        get() = Clamping.CLAMP

    override var depthFunc: DepthMode?
        get() = null
        set(value) {}

    override val filtering: Filtering
        get() = Filtering.TRULY_LINEAR

    override val width: Int
        get() = 512
    override val height: Int
        get() = 512

    override val internalFormat: Int
        get() = GL_RGBA
    override val isDestroyed: Boolean
        get() = false
    override val isHDR: Boolean
        get() = false
    override val locallyAllocated: Long
        get() = 0L
    override val name: String
        get() = "SurfaceTexture[$pointer]"
    override val samples: Int
        get() = 1
    override val wasCreated: Boolean
        get() = pointer >= 0 && GFXState.session == session

    override fun bind(index: Int, filtering: Filtering, clamping: Clamping): Boolean {
        checkSession()
        Texture2D.activeSlot(index)
        return Texture2D.bindTexture(TARGET, pointer)
    }

    override fun checkSession() {
        if (pointer < 0 || session != GFXState.session) {
            session = GFXState.session
            pointer = createExternalTexture()
        }
    }

}