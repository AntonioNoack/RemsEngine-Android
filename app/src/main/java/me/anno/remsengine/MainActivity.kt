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
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import me.anno.Logging
import me.anno.config.DefaultConfig
import me.anno.config.DefaultStyle
import me.anno.gpu.OpenGL
import me.anno.gpu.buffer.Buffer
import me.anno.gpu.debug.DebugGPUStorage
import me.anno.gpu.drawing.DrawRectangles.drawRect
import me.anno.gpu.shader.OpenGLShader
import me.anno.gpu.texture.Texture2D
import me.anno.input.Input
import me.anno.input.Touch
import me.anno.remsengine.android.KeyMap
import me.anno.remsstudio.RemsStudio
import me.anno.studio.StudioBase
import me.anno.studio.StudioBase.Companion.addEvent
import me.anno.utils.Clock
import me.anno.utils.LOGGER
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import kotlin.math.log
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    val renderer = Renderer()

    private lateinit var glSurfaceView: GLSurfaceView

    private var engine: StudioBase? = null

    private val logger = LogManager.getLogger(MainActivity::class)

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

        logger.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        logger.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        logger.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        logger.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")

        val engine = this.engine ?: RemsStudio // RemsEngine()

        StudioBase.instance = engine
        engine.setupNames()
        engine.tick("run")
        Logging.setup()
        engine.tick("logging")
        GFX.gameInit = engine::gameInit
        GFX.gameLoop = engine::onGameLoop
        GFX.onShutdown = engine::onShutdown
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

        logger.info("OpenGL ES Version: $major.$minor")

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(major)
            glSurfaceView.setRenderer(renderer)
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if we want to support both ES 1 and ES 2.
            logger.error("Rem's Engine currently does not support OpenGL ES 1.0")
        }

        setContentView(glSurfaceView)

        detector = GestureDetectorCompat(this, this)
        detector.setOnDoubleTapListener(this)

    }

    lateinit var detector: GestureDetectorCompat

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
            Input.onMousePress(GLFW_MOUSE_BUTTON_RIGHT)
            Input.onMouseRelease(GLFW_MOUSE_BUTTON_RIGHT)
            logger.info("Long Press")
        }
    }

    override fun onShowPress(e: MotionEvent?) {
        // mmh...
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        // double click, but we have implemented that ourselves anyways
        GFX.addGPUTask(1) { DebugGPUStorage.openMenu() }
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
        addEvent { Input.onKeyPressed(keyCode) }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        addEvent { Input.onKeyReleased(keyCode) }
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
                if (isMouse) Input.onMouseMove(x, y)
            }
            // only if there is a single pointer?
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> addEvent {
                Touch.Companion.onTouchDown(pid, x, y)
                if (isMouse) Input.onMousePress(GLFW_MOUSE_BUTTON_LEFT)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> addEvent {
                Touch.Companion.onTouchUp(pid, x, y)
                if (isMouse) Input.onMouseRelease(GLFW_MOUSE_BUTTON_LEFT)
                // update mouse position, when the gesture is finished (no more touches down)?
            }
        }
        detector.onTouchEvent(event)
        return true
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

    companion object {

        var lastMouseX = 0f
        var lastMouseY = 0f

        const val GLFW_MOUSE_BUTTON_LEFT = 0
        const val GLFW_MOUSE_BUTTON_RIGHT = 1
        const val GLFW_MOUSE_BUTTON_MIDDLE = 2

        fun Any.setProperty(name: String, value: Any?) {
            val property = this::class.memberProperties
                .first { it.name == name }
                .apply { isAccessible = true }
            property as KMutableProperty1<Any, Any?>
            property.set(this, value)
        }

    }

}