package me.anno.remsengine

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
import me.anno.engine.RemsEngine
import me.anno.gpu.GFX
import me.anno.gpu.WindowX
import me.anno.gpu.debug.DebugGPUStorage
import me.anno.input.Input
import me.anno.input.Touch
import me.anno.remsengine.android.KeyMap.keyCodeMapping
import me.anno.studio.StudioBase
import me.anno.studio.StudioBase.Companion.addEvent
import me.anno.utils.Logging
import me.anno.utils.OS
import org.apache.logging.log4j.LogManager
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val renderer = Renderer()

    private lateinit var glSurfaceView: GLSurfaceView

    private var engine: StudioBase? = null

    private val windowX = WindowX("")

    init {
        GFX.windows.clear()
        GFX.windows.add(windowX)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide top bar
        supportActionBar?.hide()

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

        LOGGER.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        LOGGER.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        LOGGER.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        LOGGER.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")

        val engine = this.engine ?: RemsEngine()

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

        LOGGER.info("OpenGL ES Version: $major.$minor")

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(major)
            glSurfaceView.setRenderer(renderer)
            glSurfaceView.renderMode = RENDERMODE_CONTINUOUSLY
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if we want to support both ES 1 and ES 2.
            LOGGER.error("Rem's Engine currently does not support OpenGL ES 1.0")
        }

        setContentView(glSurfaceView)

        detector = GestureDetectorCompat(this, this)
        detector.setOnDoubleTapListener(this)

    }

    private lateinit var detector: GestureDetectorCompat

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // todo how can we use that, would be ever use it?
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        addEvent {
            Input.onMousePress(windowX, GLFW_MOUSE_BUTTON_RIGHT)
            Input.onMouseRelease(windowX, GLFW_MOUSE_BUTTON_RIGHT)
            LOGGER.info("Long Press")
        }
    }

    override fun onShowPress(e: MotionEvent?) {
        // mmh...
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        // double click, but we have implemented that ourselves anyways
        addEvent { DebugGPUStorage.openMenu() }
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // addEvent { Input.onMouseWheel(distanceX, distanceY, false) }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        addEvent { Input.onKeyPressed(windowX, keyCodeMapping[keyCode] ?: keyCode) }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        addEvent { Input.onKeyReleased(windowX, keyCodeMapping[keyCode] ?: keyCode) }
        return true
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        println("generic motion event")
        return super.onGenericMotionEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        val pid = event.getPointerId(event.actionIndex)
        val isMouse = pid == 0
        val x = event.x
        val y = event.y - 80f // why ever
        if (lastMouseX != x || lastMouseY != y) {
            lastMouseX = x
            lastMouseY = y
            addEvent {
                Touch.Companion.onTouchMove(pid, x, y)
                if (isMouse) Input.onMouseMove(windowX, x, y)
            }
            // only if there is a single pointer?
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> addEvent {
                Touch.Companion.onTouchDown(pid, x, y)
                if (isMouse) Input.onMousePress(windowX, GLFW_MOUSE_BUTTON_LEFT)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> addEvent {
                Touch.Companion.onTouchUp(pid, x, y)
                if (isMouse) Input.onMouseRelease(windowX, GLFW_MOUSE_BUTTON_LEFT)
                // update mouse position, when the gesture is finished (no more touches down)?
            }
        }
        detector.onTouchEvent(event)
        return true
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
        LOGGER.info("Resumed")
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
        LOGGER.info("Paused")
    }

    companion object {

        private val LOGGER = LogManager.getLogger(MainActivity::class)

        var lastMouseX = 0f
        var lastMouseY = 0f

        const val GLFW_MOUSE_BUTTON_LEFT = 0
        const val GLFW_MOUSE_BUTTON_RIGHT = 1
        const val GLFW_MOUSE_BUTTON_MIDDLE = 2
        const val GLFW_KEY_ESCAPE = 256

        fun Any.setProperty(name: String, value: Any?) {
            val property = this::class.memberProperties
                .first { it.name == name }
                .apply { isAccessible = true }
            @Suppress("unchecked_cast")
            property as KMutableProperty1<Any, Any?>
            property.set(this, value)
        }

    }

}