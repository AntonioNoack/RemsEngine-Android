package me.anno.remsengine.android

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import me.anno.Time
import me.anno.config.DefaultConfig
import me.anno.engine.Events.addEvent
import me.anno.gpu.GFX
import me.anno.gpu.GFXBase
import me.anno.gpu.GFXState
import me.anno.gpu.Logo.drawLogo
import me.anno.gpu.Logo.logoBackgroundColor
import me.anno.gpu.drawing.DrawRectangles
import me.anno.input.Touch
import me.anno.remsengine.android.MainActivity.Companion.setStatic
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.hasExtension
import org.lwjgl.opengl.GL11.testShaderVersions
import org.lwjgl.opengl.GL11C
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer : GLSurfaceView.Renderer {

    private val logger = LogManager.getLogger(Renderer::class)

    companion object {
        fun newSession1() {
            GFXState.newSession()
            GL11.invalidateBinding()
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
        val window = GFX.someWindow
        if (width != window.width || height != window.height) {
            window.width = width
            window.height = height
            addEvent {
                window.framesSinceLastInteraction = 0
            }
        }
    }

    private var frameIndex = 0
    private val numLogoFrames = 8
    override fun onDrawFrame(gl: GL10?) {
        // run drawing function
        try {
            // neither true nor false work without issues
            DefaultConfig["ui.sparseRedraw"] = true
            // frame isn't kept on Android :/
            GFX.someWindow.needsRefresh = true
            logoBackgroundColor = 0xff99aaff.toInt()
            GFX.glThread = Thread.currentThread()
            val windowX = GFX.someWindow
            GFX.activeWindow = windowX
            GFX.check()

            GL11C.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            GL11C.glViewport(0, 0, windowX.width, windowX.height)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST) // todo why aren't we setting this?

            when (frameIndex++) {
                0 -> {
                    val cap = GL.getCapabilities()
                    GFXBase.setStatic("capabilities", cap)
                    cap.GL_ARB_depth_texture = hasExtension("OES_depth_texture")
                    GFX.maxSamples = 1 // max(1, GL11C.glGetInteger(GL_MAX_SAMPLES))
                    // my emulator says 4, but only supports OpenGL ES 3.0...
                    // if (version10x < 31) GFX.maxSamples = 1
                    drawLogo(windowX.width, windowX.height, false)
                }
                in 1 until numLogoFrames -> {
                    drawLogo(windowX.width, windowX.height, false)
                }
                numLogoFrames -> {
                    drawLogo(windowX.width, windowX.height, true)
                    KeyMap.defineKeys()
                    GFX.check()
                    GFXBase.init2(null)
                }
                else -> {
                    GFXBase.updateWindows()
                    Time.updateTime()
                    // Input.pollControllers(windowX)
                    Touch.updateAll()
                    GFX.activeWindow = windowX
                    GFX.renderStep(windowX, true)
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