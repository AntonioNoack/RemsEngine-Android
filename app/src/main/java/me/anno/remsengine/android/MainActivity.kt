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
import me.anno.Time
import me.anno.config.DefaultConfig.style
import me.anno.ecs.Component
import me.anno.ecs.Entity
import me.anno.ecs.annotations.DebugAction
import me.anno.ecs.components.light.sky.SkyboxBase
import me.anno.ecs.components.mesh.MeshComponent
import me.anno.engine.DefaultAssets
import me.anno.engine.EngineBase
import me.anno.engine.Events.addEvent
import me.anno.engine.ui.control.DraggingControls
import me.anno.engine.ui.render.PlayMode
import me.anno.engine.ui.render.RenderMode
import me.anno.engine.ui.render.RenderMode.Companion.opaqueNodeSettings
import me.anno.engine.ui.render.RenderView
import me.anno.engine.ui.render.RenderView0
import me.anno.engine.ui.render.SceneView
import me.anno.engine.ui.render.SceneView.Companion.testScene
import me.anno.engine.ui.scenetabs.ECSSceneTab
import me.anno.engine.ui.scenetabs.ECSSceneTabs
import me.anno.gpu.GFX
import me.anno.gpu.GPUTasks
import me.anno.graph.visual.render.QuickPipeline
import me.anno.graph.visual.render.effects.SSAONode
import me.anno.graph.visual.render.scene.RenderDeferredNode
import me.anno.input.Input
import me.anno.input.Key
import me.anno.io.saveable.Saveable.Companion.registerCustomClass
import me.anno.remsengine.android.KeyMap.keyCodeMapping
import me.anno.ui.debug.TestEngine
import me.anno.utils.Logging
import me.anno.utils.OS
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11

// todo create some small games to show our engine's capabilities
//  - most 2d, because mobile
//  - some easy 3d
//  best just port them from our tests section

// todo open keyboard when in text input

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val renderer = Renderer()

    private var glSurfaceView: GLSurfaceView? = null

    val osWindow = GFX.someWindow

    init {
        GFX.windows.clear()
        GFX.windows.add(osWindow)
    }

    class TestControls : Component() {
        @DebugAction
        fun resetView() {
            val instance = RenderView.currentInstance!!
            val controls = instance.controlScheme as DraggingControls
            controls.resetCamera()
            instance.radius = 3.0
        }
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

        AndroidPlugin.onEnable()

        val src0 = OS.documents
        val src1 = src0.getChild("RemsEngine")
        val src2 = src1.getChild("SampleProject")

        LOGGER.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        LOGGER.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        LOGGER.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        LOGGER.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")

        registerCustomClass(TestControls())

        val scene = Entity()
        // scene.add(Skybox().apply { cumulus = 0f; cirrus = 0f })
        scene.add(SkyboxBase())
        // scene.add(MeshComponent(IcosahedronModel.createIcosphere(4)))
        scene.add(MeshComponent(DefaultAssets.flatCube))
        scene.add(TestControls())

        val renderMode = if (true) {
            testRenderMode.value
        } else RenderMode.SIMPLE
        val engine = TestEngine("Rem's Engine") {
            val p = if (true) {
                testScene(scene) {
                    it.renderView.renderMode = renderMode
                }
            } else {
                ECSSceneTabs.open(ECSSceneTab(scene.ref, PlayMode.EDITING), true)
                val p = SceneView(RenderView0(PlayMode.EDITING, style), style)
                p.renderView.renderMode = renderMode
                p
            }
            listOf(p.fill(1f))
        }

        EngineBase.instance = engine
        EngineBase.showFPS = true
        engine.setupNames()
        engine.tick("run")
        // GFX.onInit = engine::gameInit
        // GFX.onLoop = engine::onGameLoop
        // GFX.onShutdown = engine::onShutdown
        GPUTasks.gpuTasks.clear() // they couldn't be executed anyways
        engine.loadConfig()
        engine.tick("config")

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
        e1: MotionEvent?,
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
        // addEvent { DebugGPUStorage.openMenu() }
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
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
            val time = Time.nanoTime
            addEvent { Input.onKeyPressed(osWindow, key, time) }
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
        GFX.glThread = Thread.currentThread()
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

        val testRenderMode = lazy {
            RenderMode(
                "Testing",
                QuickPipeline()
                    .then1(RenderDeferredNode(), opaqueNodeSettings)
                    .then(
                        SSAONode(),
                        mapOf("Strength" to 1000f),
                        mapOf("Ambient Occlusion" to listOf("Illuminated"))
                    )
                    .finish()
            )
        }

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