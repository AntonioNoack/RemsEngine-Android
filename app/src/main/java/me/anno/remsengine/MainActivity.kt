package me.anno.remsengine

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.anno.engine.RemsEngine
import me.anno.gpu.GFX
import me.anno.utils.OS
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.content.ContextWrapper
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import me.anno.Logging
import me.anno.config.DefaultConfig
import me.anno.gpu.OpenGL
import me.anno.gpu.buffer.Buffer
import me.anno.gpu.shader.OpenGLShader
import me.anno.gpu.texture.Texture2D
import me.anno.input.Input
import me.anno.input.MouseButton
import me.anno.input.Touch
import me.anno.studio.StudioBase
import me.anno.utils.LOGGER
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import kotlin.math.log

class MainActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    // todo input events: we can call the glfw events, or the input events :)

    private lateinit var glSurfaceView: GLSurfaceView

    private lateinit var engine: RemsEngine

    private val logger = LogManager.getLogger(MainActivity::class)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)

        val c = ContextWrapper(this)
        val path = c.filesDir.path
        System.setProperty("user.home", path)

        OS.isAndroid = true
        OS.isWindows = false
        OS.isLinux = false

        val src0 = OS.documents
        val src1 = src0.getChild("RemsEngine")
        val src2 = src1.getChild("SampleProject")

        logger.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        logger.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        logger.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        logger.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")

        engine = RemsEngine()
        StudioBase.instance = engine
        engine.setupNames()
        engine.tick("run")
        Logging.setup()
        engine.tick("logging")
        GFX.gameInit = engine::gameInit
        GFX.gameLoop = engine::onGameLoop
        GFX.onShutdown = engine::onShutdown
        engine.loadConfig()
        engine.tick("config")
        // GFX.run()
        // engine.run()

        // Check if the system supports OpenGL ES 2.0.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val version = configurationInfo.reqGlEsVersion
        val supportsEs2 = version >= 0x20000
        val major = version.shr(16)
        val minor = version.and(0xffff)

        logger.info("OpenGL ES Version: $major.$minor")

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(major)
            glSurfaceView.setRenderer(this)
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            logger.error("Rem's Engine currently does not support OpenGL ES 1.0")
        }

        setContentView(glSurfaceView)
        Input
        glSurfaceView.setOnTouchListener { _, event ->
            val pid = event.getPointerId(event.actionIndex)
            println("pid ${event.action}, ${event.x} ${event.y}")
            when (event.action) {
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_HOVER_MOVE -> {
                    Touch.Companion.onTouchMove(pid, event.x, event.y)
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    Touch.Companion.onTouchUp(pid, event.x, event.y)
                }
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_POINTER_DOWN -> {
                    Touch.Companion.onTouchDown(pid, event.x, event.y)
                }
            }
            // todo we need to call click & such
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // all old resources have become worthless
        OpenGL.newSession()

        GL11.testShaderVersions()

        // Buffer.useVAOs = false
        // Buffer.alwaysBindBuffer = true
        Texture2D.alwaysBindTexture = true

        logger.info("Surface Created")

        frameIndex = 0

        // init opengl and such
        GFX.check()

        /*val arrayBuffer = GL20.glGenBuffers()
        GL20.glBindBuffer(GL_ARRAY_BUFFER, arrayBuffer)
        val bytes = ByteBuffer.allocateDirect(400)
        bytes.position(0).limit(bytes.capacity())
        GL20.glBufferData(GL_ARRAY_BUFFER, bytes, GL_STATIC_DRAW)
        val elementBuffer = GL20.glGenBuffers()
        GL20.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer)
        bytes.position(0).limit(bytes.capacity())
        GL20.glBufferData(GL_ELEMENT_ARRAY_BUFFER, bytes, GL_STATIC_DRAW)
        GFX.check()
        val vao = GL20.glGenVertexArrays()
        GL20.glBindVertexArray(vao)
        GL20.glVertexAttribPointer(0, 4, GL_FLOAT, false, 4, 0)
        GL20.glVertexAttribDivisor(0, 1)
        GL20.glEnableVertexAttribArray(0)
        GFX.check()
        GL20.glDrawArrays(4, 0, 3)
        GFX.check()
        GLES30.glDrawArraysInstanced(4, 0, 3, 1)
        GFX.check()
        GL20.glDrawElements(4, 1, GL_UNSIGNED_INT, 0)
        GFX.check()
        GL20.glDrawElementsInstanced(4, 1, GL_UNSIGNED_INT, 0, 1)
        GFX.check()*/
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GFX.width = width
        GFX.height = height
        Input.invalidateLayout();
    }

    private fun invalidateOpenGLES() {
        OpenGLShader.lastProgram = -1
        Texture2D.invalidateBinding()
        Buffer.invalidateBinding()
    }

    var frameIndex = 0
    override fun onDrawFrame(gl: GL10?) {
        // run drawing function
        try {
            logger.info("Drawing Frame ${GFX.width} x ${GFX.height}")
            invalidateOpenGLES()
            when (frameIndex++) {
                0 -> {
                    GFX.check()
                    GFX.renderFrame0()
                    GFX.check()
                }
                1 -> {
                    GFX.check()
                    GFX.renderStep0()
                    // my emulator says 4, but only supports OpenGL ES 3.0...
                    if (GL11.version10x < 31) {
                        GFX.maxSamples = 1
                    }
                    GFX.check()
                    GFX.gameInit.invoke()
                    GFX.check()
                }
                else -> {
                    GFX.check()
                    GFX.renderStep()
                    GFX.check()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.error("Shutting down because of $e")
            Thread.sleep(1000)
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
        logger.info("Resumed")
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
        logger.info("Paused")
    }

}