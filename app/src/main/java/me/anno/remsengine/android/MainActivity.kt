package me.anno.remsengine.android

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY
import android.os.Bundle
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import me.anno.config.DefaultConfig.style
import me.anno.ecs.components.shaders.Skybox
import me.anno.engine.RemsEngine
import me.anno.engine.ui.render.Renderers.previewRenderer
import me.anno.gpu.GFX
import me.anno.gpu.GFXState.useFrame
import me.anno.gpu.OSWindow
import me.anno.gpu.debug.DebugGPUStorage
import me.anno.gpu.drawing.Perspective
import me.anno.input.Input
import me.anno.input.Key
import me.anno.io.files.thumbs.Thumbs
import me.anno.io.files.thumbs.ThumbsExt
import me.anno.remsengine.android.KeyMap.keyCodeMapping
import me.anno.studio.StudioBase
import me.anno.studio.StudioBase.Companion.addEvent
import me.anno.tests.game.Snake
import me.anno.ui.Panel
import me.anno.ui.debug.TestStudio
import me.anno.utils.Logging
import me.anno.utils.OS
import org.apache.logging.log4j.LogManager
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

// todo we need depth texture support
//  -> check if we have fp16/32 texture support
//  -> add a deferred layer for that, and link it in the corresponding framebuffers
//  -> fill it in the shader properly: gl_FragCoord.z probably

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val renderer = Renderer()

    private var glSurfaceView: GLSurfaceView? = null

    private var engine: StudioBase? = null

    val osWindow = OSWindow("")

    init {
        GFX.windows.clear()
        GFX.windows.add(osWindow)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide top bar
        supportActionBar?.hide()

        val c = ContextWrapper(this)
        val path = c.filesDir.path
        System.setProperty("user.home", path)

        OS.isAndroid = true
        OS.isWindows = false
        OS.isLinux = false

        val src0 = OS.documents
        val src1 = src0.getChild("RemsEngine")
        val src2 = src1.getChild("SampleProject")

        LOGGER.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        LOGGER.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        LOGGER.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        LOGGER.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")

        this.engine = RemsEngine()
        val engine = this.engine ?: TestStudio("Android") {
            val skyPanel = object : Panel(style) {
                val sky = Skybox()
                val cameraMatrix = Matrix4f()
                var first = true
                override val canDrawOverBorders get() = true
                override fun onDraw(x0: Int, y0: Int, x1: Int, y1: Int) {
                    useFrame(previewRenderer) {
                        sky.nadirSharpness = 10f
                        val shader = sky.shader!!.value
                        shader.use()
                        if (first) shader.printCode()
                        first = false
                        Perspective.setPerspective(
                            cameraMatrix,
                            0.7f,
                            (x1 - x0) * 1f / (y1 - y0),
                            0.001f, 10f, 0f, 0f
                        )
                        ThumbsExt.bindShader(shader, cameraMatrix, Thumbs.matModelMatrix)
                        sky.material.bind(shader)
                        sky.draw(shader, 0)
                    }
                }
            }.apply { weight = 1f }
            val snakePanel = Snake().apply { weight = 1f }
            listOf(skyPanel)
        }

        StudioBase.instance = engine
        engine.setupNames()
        engine.tick("run")
        Logging.setup()
        engine.tick("logging")
        // GFX.onInit = engine::gameInit
        // GFX.onLoop = engine::onGameLoop
        // GFX.onShutdown = engine::onShutdown
        GFX.gpuTasks.clear() // they couldn't be executed anyways
        engine.loadConfig()
        engine.tick("config")
        this.engine = engine

        // Check if the system supports OpenGL ES 2.0.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val version = configurationInfo.reqGlEsVersion
        val supportsEs2 = version >= 0x20000
        val major = version.shr(16)
        val minor = version.and(0xffff)

        GL11.setVersion(major, minor)
        LOGGER.info("OpenGL ES Version: $major.$minor")

        if (supportsEs2) {
            val glSurfaceView = SurfaceView(this)
            this.glSurfaceView = glSurfaceView
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(major)
            glSurfaceView.setRenderer(renderer)
            glSurfaceView.renderMode = RENDERMODE_CONTINUOUSLY
            setContentView(glSurfaceView)
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if we want to support both ES 1 and ES 2.
            LOGGER.error("Rem's Engine currently does not support OpenGL ES 1.0")
            this.glSurfaceView = null
        }

        detector = GestureDetectorCompat(this, this)
        detector.setOnDoubleTapListener(this)

    }

    lateinit var detector: GestureDetectorCompat

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // todo how can we use that, would be ever use it?
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        addEvent {
            Input.onMousePress(osWindow, Key.BUTTON_RIGHT)
            Input.onMouseRelease(osWindow, Key.BUTTON_RIGHT)
            LOGGER.info("Long Press")
        }
    }

    override fun onShowPress(e: MotionEvent) {
        // mmh...
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        // double click, but we have implemented that ourselves anyways
        addEvent { DebugGPUStorage.openMenu() }
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // addEvent { Input.onMouseWheel(distanceX, distanceY, false) }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        println("Key-down: $keyCode -> ${keyCodeMapping[keyCode]}")
        val key = keyCodeMapping[keyCode]
        if (key != null) {
            addEvent { Input.onKeyPressed(osWindow, key) }
        }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val key = keyCodeMapping[keyCode]
        if (key != null) {
            addEvent { Input.onKeyReleased(osWindow, key) }
        }
        return true
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        println("generic motion event")
        return super.onGenericMotionEvent(event)
    }

    override fun onResume() {
        super.onResume()
        Renderer.newSession1()
        glSurfaceView?.onResume()
        LOGGER.info("Resumed")
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
        LOGGER.info("Paused")
    }

    companion object {

        private val LOGGER = LogManager.getLogger(MainActivity::class)

        var lastMouseX = 0f
        var lastMouseY = 0f

        const val GLFW_MOUSE_BUTTON_LEFT = 0
        const val GLFW_MOUSE_BUTTON_RIGHT = 1
        const val GLFW_MOUSE_BUTTON_MIDDLE = 2

        fun Any.setStatic(name: String, value: Any?) {
            val property = this.javaClass.getField(name)
            property.isAccessible = true
            property.set(null, value)
        }

    }

}