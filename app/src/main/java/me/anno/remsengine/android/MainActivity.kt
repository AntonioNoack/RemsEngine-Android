package me.anno.remsengine.android

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import me.anno.Time
import me.anno.config.DefaultConfig.style
import me.anno.ecs.Entity
import me.anno.ecs.components.light.sky.SkyboxBase
import me.anno.ecs.components.mesh.MeshComponent
import me.anno.engine.DefaultAssets
import me.anno.engine.EngineBase
import me.anno.engine.Events.addEvent
import me.anno.engine.WindowRenderFlags
import me.anno.engine.ui.render.PlayMode
import me.anno.engine.ui.render.RenderMode
import me.anno.engine.ui.render.RenderView0
import me.anno.engine.ui.render.SceneView
import me.anno.engine.ui.render.SceneView.Companion.testScene
import me.anno.engine.ui.scenetabs.ECSSceneTab
import me.anno.engine.ui.scenetabs.ECSSceneTabs
import me.anno.gpu.GFX
import me.anno.gpu.GPUTasks
import me.anno.input.Input
import me.anno.io.saveable.Saveable.Companion.registerCustomClass
import me.anno.remsengine.android.KeyMap.keyCodeMapping
import me.anno.remsengine.android.test.TestControls
import me.anno.remsengine.android.test.TestRenderMode.testRenderMode
import me.anno.ui.debug.TestEngine
import me.anno.utils.GFXFeatures
import me.anno.utils.OS
import me.anno.utils.OSFeatures
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import kotlin.test.assertTrue

// todo create some small games to show our engine's capabilities
//  - most 2d, because mobile
//  - some easy 3d
//  best just port them from our tests section

// todo open keyboard when in text input

class MainActivity : AppCompatActivity() {

    private val renderer = Renderer()
    private var glSurfaceView: GLSurfaceView? = null

    lateinit var detector: GestureDetectorCompat

    val osWindow = GFX.someWindow

    init {
        GFX.windows.clear()
        GFX.windows.add(osWindow)
    }

    private fun defineFeatures() {
        OS.isAndroid = true
        OS.isWindows = false
        OS.isLinux = false

        OSFeatures.canSleep = true
        OSFeatures.canHostServers = true
        OSFeatures.hasMultiThreading = true
        OSFeatures.supportsNetworkUDP = true // I think so
        OSFeatures.supportsContinuousLogFiles = false // don't want that for now
        OSFeatures.filesAreCaseSensitive = true // yes, Linux-like

        GFXFeatures.isOpenGLES = true
        GFXFeatures.canToggleVSync = false
        GFXFeatures.canOpenNewWindows = false
        GFXFeatures.hasWeakGPU = true
    }

    private fun createEngineInstance(): EngineBase {

        registerCustomClass(TestControls())

        val scene = Entity()
        // scene.add(Skybox().apply { cumulus = 0f; cirrus = 0f })
        scene.add(SkyboxBase())
        // scene.add(MeshComponent(IcosahedronModel.createIcosphere(4)))
        scene.add(MeshComponent(DefaultAssets.icoSphere))
        scene.add(TestControls())

        val renderMode = if (true) {
            testRenderMode.value
        } else RenderMode.SIMPLE
        return TestEngine("Rem's Engine") {
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
    }

    private fun defineHome() {
        val c = ContextWrapper(this)
        val path = c.filesDir.path
        System.setProperty("user.home", path)
    }

    private fun defineFolders() {
        val src0 = OS.documents
        val src1 = src0.getChild("RemsEngine")
        val src2 = src1.getChild("SampleProject")

        LOGGER.info("home: ${OS.home}, ${OS.home.exists}, ${OS.home.mkdirs()}")
        LOGGER.info("s0: $src0, ${src0.exists}, ${src0.mkdirs()}")
        LOGGER.info("s1: $src1, ${src1.exists}, ${src1.mkdirs()}")
        LOGGER.info("s2: $src2, ${src2.exists}, ${src2.mkdirs()}")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide top bar
        supportActionBar?.hide()

        defineHome()
        defineFeatures()
        defineFolders()

        AndroidPlugin.onEnable()

        setupEngine()
        setupOpenGLES20()

        val handler = GestureHandler()
        detector = GestureDetectorCompat(this, handler)
        detector.setOnDoubleTapListener(handler)
    }

    private fun setupEngine() {
        val engine = createEngineInstance()
        EngineBase.instance = engine
        WindowRenderFlags.showFPS = true
        engine.setupNames()
        engine.tick("run")
        GPUTasks.gpuTasks.clear() // they couldn't be executed anyways
        engine.loadConfig()
        engine.tick("config")
    }

    private fun setupOpenGLES20() {
        // Check if the system supports OpenGL ES 2.0.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val version = configurationInfo.reqGlEsVersion
        val supportsEs20 = version >= 0x20000
        val supportsEs32 = version >= 0x30002
        val major = version.shr(16)
        val minor = version.and(0xffff)

        GFXFeatures.supportsTextureGather = supportsEs32

        GL11.setVersion(major, minor)
        LOGGER.info("OpenGL ES Version: $major.$minor")

        // guaranteed since Android 2.2 (API level 8), aka 2010;
        // we also define it in the AndroidManifest, so this should be a given
        assertTrue(supportsEs20, "Must support OpenGL ES 2.0")

        val glSurfaceView = SurfaceView(this)
        this.glSurfaceView = glSurfaceView
        // Request an OpenGL ES 2.0 compatible context.
        glSurfaceView.setEGLContextClientVersion(major)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = RENDERMODE_CONTINUOUSLY
        setContentView(glSurfaceView)
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