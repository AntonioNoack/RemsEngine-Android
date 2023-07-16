package me.anno.remsengine.android

import android.opengl.GLES20
import android.opengl.GLES30.GL_MAX_SAMPLES
import android.opengl.GLSurfaceView
import android.os.Build
import me.anno.Engine
import me.anno.config.DefaultConfig
import me.anno.config.DefaultStyle
import me.anno.gpu.*
import me.anno.gpu.drawing.DrawRectangles
import me.anno.input.Input
import me.anno.remsengine.android.MainActivity.Companion.setStatic
import me.anno.studio.StudioBase
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11C
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max

class Renderer : GLSurfaceView.Renderer {

    private val logger = LogManager.getLogger(Renderer::class)

    companion object {
        fun newSession1() {
            GFXState.newSession()
            invalidateBinding()
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GFX.glThread = Thread.currentThread()
        GFX.maxBoundTextures = 1 // temporary
        println("///////////// next session //////////////////")
        newSession1()
        testShaderVersions()
        // Texture2D.alwaysBindTexture = true
        logger.info("Surface Created")
        frameIndex = 0
        GFX.check()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val window = GFX.someWindow!!
        if (width != window.width || height != window.height) {
            window.width = width
            window.height = height
            StudioBase.addEvent {
                window.framesSinceLastInteraction = 0
            }
        }
    }

    private fun invalidateOpenGLES() {
        // OpenGLShader.invalidateBinding()
        // Texture2D.invalidateBinding()
        // OpenGLBuffer.invalidateBinding()
    }

    private var frameIndex = 0
    private val numLogoFrames = 2
    override fun onDrawFrame(gl: GL10?) {
        // run drawing function
        try {
            // neither true nor false work without issues
            DefaultConfig["ui.sparseRedraw"] = true
            // frame isn't kept on Android :/
            GFX.windows.firstOrNull()?.needsRefresh = true
            logoBackgroundColor = 0xff99aaff.toInt()
            GFX.glThread = Thread.currentThread()
            invalidateOpenGLES()
            val windowX = GFX.someWindow!!
            GFX.activeWindow = windowX
            GFX.check()

            GL11C.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            GL11C.glViewport(0, 0, windowX.width, windowX.height)

            // todo adjust to work like in Browser

            when (val fi = frameIndex++) {
                0 -> {
                    println("Initializing graphics")
                    // GFXBase.prepareForRendering(null)
                    GFXBase.setStatic("capabilities", GL.getCapabilities())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        GFX.maxSamples = max(1, GL11C.glGetInteger(GL_MAX_SAMPLES))
                    } else GFX.maxSamples = 1
                    // my emulator says 4, but only supports OpenGL ES 3.0...
                    if (version10x < 31) GFX.maxSamples = 1
                    drawLogo(windowX.width, windowX.height, false)
                }
                in 1 until numLogoFrames -> {
                    println("Drawing logo $fi/$numLogoFrames")
                    drawLogo(windowX.width, windowX.height, false)
                }
                numLogoFrames -> {
                    // println("Drawing last logo frame")
                    drawLogo(windowX.width, windowX.height, true)
                    KeyMap.defineKeys()
                    DefaultStyle.baseTheme["fontSize", "dark"] = 25
                    DefaultStyle.baseTheme["fontSize", "light"] = 25
                    DefaultStyle.baseTheme["customList.spacing", "dark"] = 10
                    DefaultStyle.baseTheme["customList.spacing", "light"] = 10
                    GFX.check()
                    GFXBase.init2(null)
                    GFX.supportsDepthTextures = false
                }
                else -> {
                    // println("Drawing window $fi")
                    // todo check whether this still works
                    GFXBase.updateWindows()
                    Engine.updateTime()
                    Input.pollControllers(windowX)
                    GFX.activeWindow = windowX
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