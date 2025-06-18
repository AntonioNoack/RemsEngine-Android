package me.anno.remsengine.android

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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
import me.anno.ui.Panel
import me.anno.ui.base.groups.PropertyTablePanel
import me.anno.ui.base.text.TextPanel
import me.anno.ui.debug.TestEngine
import me.anno.ui.input.InputPanel
import me.anno.ui.input.TextInput
import me.anno.ui.input.TextInputML
import me.anno.ui.input.components.PureTextInput
import me.anno.ui.input.components.PureTextInputML
import me.anno.utils.GFXFeatures
import me.anno.utils.OS
import me.anno.utils.OSFeatures
import me.anno.utils.types.AnyToBool
import me.anno.utils.types.AnyToDouble
import me.anno.utils.types.AnyToFloat
import me.anno.utils.types.AnyToInt
import me.anno.utils.types.AnyToLong
import me.anno.utils.types.Strings.isNotBlank2
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import java.math.BigDecimal
import kotlin.test.assertTrue

// todo create some small games to show our engine's capabilities
//  - most 2d, because mobile
//  - some easy 3d
//  best just port them from our tests section

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

    private fun findTitle(panel0: Panel?): String {
        var panel = panel0
        while (panel != null) {

            val placeholder = when (panel) {
                is PureTextInput -> panel.placeholder
                is PureTextInputML -> panel.placeholder
                else -> ""
            }
            if (placeholder.isNotBlank2()) return placeholder

            val tooltip = panel.tooltip
            if (tooltip.isNotBlank2()) return tooltip

            val parent = panel.uiParent
            if (parent is PropertyTablePanel) {
                val nameIndex = panel.indexInParent - 1
                val namePanel = parent.children.getOrNull(nameIndex)
                if (namePanel is TextPanel) {
                    val name = namePanel.text
                    if (name.isNotBlank2()) {
                        return name
                    }
                }
            }

            panel = parent
        }
        return "Enter Value"
    }

    private var lastDialog: AlertDialog? = null
    fun requestKeyboard(inFocus0: Panel, panel: InputPanel<*>) {
        val oldValue = panel.value
        val title = findTitle(inFocus0)

        runOnUiThread {
            lastDialog?.dismiss()
            lastDialog = null

            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)

            // Set up the input
            val input = EditText(this)
            input.inputType = when (oldValue) {
                is Float, is Double, is BigDecimal -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                is Number -> InputType.TYPE_CLASS_NUMBER
                else -> InputType.TYPE_CLASS_TEXT
            }
            input.setText(oldValue.toString())

            builder.setView(input)
            // Set up the buttons
            builder.setPositiveButton("OK") { dialog, _ ->
                val newValue = input.text.toString()
                addEvent { updateValue(panel, oldValue, newValue) }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.setOnDismissListener {
                lastDialog = null
            }
            builder.setOnCancelListener {
                lastDialog = null
            }
            lastDialog = builder.show()
        }
    }

    private fun updateValue(panel: InputPanel<*>, oldValue: Any?, newValue: String) {
        @Suppress("UNCHECKED_CAST")
        when (oldValue) {
            is String ->
                (panel as InputPanel<String>).setValue(newValue, true)
            is Double ->
                (panel as InputPanel<Double>).setValue(AnyToDouble.getDouble(newValue), true)
            is Float ->
                (panel as InputPanel<Float>).setValue(AnyToFloat.getFloat(newValue), true)
            is Long ->
                (panel as InputPanel<Long>).setValue(AnyToLong.getLong(newValue), true)
            is Int ->
                (panel as InputPanel<Int>).setValue(AnyToInt.getInt(newValue), true)
            is Boolean ->
                (panel as InputPanel<Boolean>).setValue(AnyToBool.anyToBool(newValue), true)
            else -> LOGGER.warn("Unknown/unsupported value type ${oldValue?.javaClass}")
        }
        when (panel) {
            is TextInput -> panel.setCursorToEnd()
            is TextInputML -> panel.setCursorToEnd()
            is PureTextInput -> panel.setCursorToEnd()
            is PureTextInputML -> panel.setCursorToEnd()
        }
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
        // joystick, controller, ...
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