package me.anno.remsengine.android

import android.opengl.GLES20
import android.opengl.GLES30.GL_MAX_SAMPLES
import android.opengl.GLSurfaceView
import me.anno.Time
import me.anno.config.DefaultConfig
import me.anno.engine.WindowRenderFlags
import me.anno.gpu.GFX
import me.anno.gpu.GFXState
import me.anno.gpu.Logo
import me.anno.gpu.Logo.logoBackgroundColor
import me.anno.gpu.RenderStep
import me.anno.gpu.RenderStep.renderStep
import me.anno.gpu.WindowManagement
import me.anno.gpu.drawing.DrawRectangles
import me.anno.remsengine.android.MainActivity.Companion.setStatic
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.hasExtension
import org.lwjgl.opengl.GL11.testShaderVersions
import org.lwjgl.opengl.GL11C
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renders the content onto the SurfaceView
 * */
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
        window.width = width
        window.height = height
    }

    private var frameIndex = 0
    private val numLogoFrames = 8

    override fun onDrawFrame(gl: GL10?) {
        // run drawing function
        try {
            // neither true nor false work without issues
            DefaultConfig["ui.sparseRedraw"] = true
            logoBackgroundColor = 0xff99aaff.toInt()
            GFX.glThread = Thread.currentThread()
            val window = GFX.someWindow
            GFX.activeWindow = window
            GFX.check()

            GL11C.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            GL11C.glViewport(0, 0, window.width, window.height)

            when (frameIndex++) {
                0 -> {
                    val cap = GL.getCapabilities()
                    WindowManagement.setStatic("capabilities", cap)
                    cap.GL_ARB_depth_texture = hasExtension("OES_depth_texture")
                    GFX.maxSamples = GL11C.glGetInteger(GL_MAX_SAMPLES)
                    if (!GL11.supportsDrawBuffers()) {
                        // OpenGL needs the drawBuffers()-call to be made
                        // to properly render onto multiple attachments
                        GFX.maxColorAttachments = 1
                    }
                    WindowRenderFlags.showFPS = true
                    // prepareForRendering would normally set this
                    GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                    Logo.drawLogo(window.width, window.height)
                }
                in 1 until numLogoFrames -> {
                    Logo.drawLogo(window.width, window.height)
                }
                numLogoFrames -> {
                    Logo.drawLogo(window.width, window.height)
                    Logo.destroy()
                    KeyMap.defineKeys()
                    GFX.check()
                    WindowManagement.init2(null)
                }
                else -> {
                    // from WindowManagement.runRenderLoopWithWindowUpdates() and WindowManagement.renderFrame()
                    Time.updateTime()
                    WindowManagement.updateWindows()
                    RenderStep.beforeRenderSteps()
                    GFX.activeWindow = window
                    renderStep(window, true)
                }
            }
            GFX.check()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}