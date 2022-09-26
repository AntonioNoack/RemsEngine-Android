package me.anno.remsengine

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.annotation.RequiresApi
import me.anno.Engine
import me.anno.config.DefaultConfig
import me.anno.config.DefaultStyle
import me.anno.gpu.GFX
import me.anno.gpu.GFXBase
import me.anno.gpu.GFXState
import me.anno.gpu.buffer.OpenGLBuffer
import me.anno.gpu.drawLogo
import me.anno.gpu.drawing.DrawRectangles
import me.anno.gpu.shader.OpenGLShader
import me.anno.gpu.texture.Texture2D
import me.anno.remsengine.MainActivity.Companion.setProperty
import me.anno.remsengine.android.KeyMap
import me.anno.studio.StudioBase
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer : GLSurfaceView.Renderer {

    private val logger = LogManager.getLogger(Renderer::class)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GFX.glThread = Thread.currentThread()
        GFXState.newSession()
        invalidateBinding()
        testShaderVersions()
        Texture2D.alwaysBindTexture = true
        logger.info("Surface Created")
        frameIndex = 0
        GFX.check()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val window = GFX.someWindow
        if (width != window.width || height != window.height) {
            window.width = width
            window.height = height
            StudioBase.addEvent {
                window.framesSinceLastInteraction = 0
            }
        }
    }

    private fun invalidateOpenGLES() {
        OpenGLShader.lastProgram = -1
        Texture2D.invalidateBinding()
        OpenGLBuffer.invalidateBinding()
    }

    private var frameIndex = 0
    override fun onDrawFrame(gl: GL10?) {
        // run drawing function
        try {
            // neither true nor false work without issues
            DefaultConfig["ui.sparseRedraw"] = true
            // frame isn't kept on Android :/
            GFX.windows.forEach { it.needsRefresh = true }
            GFX.glThread = Thread.currentThread()
            Engine.updateTime()
            invalidateOpenGLES()
            val windowX = GFX.someWindow
            GFX.activeWindow = windowX
            GFX.check()

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            GLES20.glViewport(0, 0, windowX.width, windowX.height)

            when (frameIndex++) {
                0 -> {
                    GFXBase.setProperty("capabilities", GL.getCapabilities())
                    // my emulator says 4, but only supports OpenGL ES 3.0...
                    if (version10x < 31) {
                        GFX.maxSamples = 1
                    }
                    drawLogo(windowX, false)
                }
                in 1 until 4 -> {
                    drawLogo(windowX, false)
                }
                4 -> {
                    drawLogo(windowX, true)
                    GFX.renderStep0()
                    if (version10x < 31) {
                        GFX.maxSamples = 1
                    }
                    KeyMap.defineKeys()
                    DefaultStyle.baseTheme["fontSize", "dark"] = 25
                    DefaultStyle.baseTheme["fontSize", "light"] = 25
                    DefaultStyle.baseTheme["customList.spacing", "dark"] = 10
                    DefaultStyle.baseTheme["customList.spacing", "light"] = 10
                    GFX.check()
                    StudioBase.instance!!.gameInit()
                }
                else -> {
                    GFX.renderStep(windowX)
                    // draw the cursor for debug purposes
                    DrawRectangles.drawRect(
                        windowX.mouseX.toInt(),
                        windowX.mouseY.toInt(),
                        6, 6, -1
                    )
                }
            }
            GFX.check()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}