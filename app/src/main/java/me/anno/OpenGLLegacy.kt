package me.anno

import android.opengl.GLES20.GL_STATIC_DRAW
import me.anno.gpu.buffer.Attribute
import me.anno.gpu.buffer.StaticBuffer
import me.anno.gpu.shader.GLSLType
import me.anno.gpu.shader.Shader
import me.anno.gpu.shader.builder.Variable
import me.anno.gpu.shader.builder.VariableMode
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11

object OpenGLLegacy {

    class Point(
        var x: Float, var y: Float, var z: Float,
        var r: Float, var g: Float, var b: Float
    ) {
        constructor(x: Float, y: Float, z: Float, color: Vector4f) :
                this(x, y, z, color.x, color.y, color.z)
    }

    private val gl1Triangles = ArrayList<Point>()
    private val gl1Attributes = ArrayList<Attribute>()
    private val gl1ModelMatrix = Matrix4f()
    private val gl1CameraMatrix = Matrix4f()
    private var gl1Shader: Shader? = null
    private val gl1Color = Vector4f()
    private var gl1MatrixMode = 0
    private var gl1DrawMode = 0

    fun glMatrixMode(mode: Int) {
        gl1MatrixMode = mode
    }


    init {
        // OpenGL 1.0 support,
        // no longer used in Rem's Engine, but added nevertheless
        val varyings = ArrayList<Variable>(1)
        varyings.add(Variable(GLSLType.V3F, "color"))
        val vsUniforms = ArrayList<Variable>(2)
        vsUniforms.add(Variable(GLSLType.M4x4, "cameraMatrix"))
        vsUniforms.add(Variable(GLSLType.M4x4, "modelMatrix"))
        vsUniforms.add(Variable(GLSLType.V3F, "pos", VariableMode.ATTR))
        vsUniforms.add(Variable(GLSLType.V3F, "col", VariableMode.ATTR))
        val fsUniforms = emptyList<Variable>()
        gl1Shader = Shader(
            "OpenGL",
            vsUniforms, "" +
                    "void main(){\n" +
                    "   gl_Position = cameraMatrix * modelMatrix * vec4(pos, 1.0);\n" +
                    "   color = col;\n" +
                    "}",
            varyings, fsUniforms,
            "void main(){ gl_FragColor = vec4(color, 1.0); }"
        )
        gl1Attributes.add(Attribute("pos", 3))
        gl1Attributes.add(Attribute("col", 3))
    }


    fun loadIdentity() {
        gl1Matrix.identity()
    }

    fun ortho(x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float) {
        gl1Matrix.ortho(x0, x1, y0, y1, z0, z1)
    }

    fun color(r: Float, g: Float, b: Float) {
        gl1Color[r, g] = b
    }

    fun begin(mode: Int) {
        gl1Triangles.clear()
        gl1DrawMode = mode
    }

    fun end() {
        // create a new buffer
        val sb = StaticBuffer(gl1Attributes, gl1Triangles.size, GL_STATIC_DRAW)
        for (p in gl1Triangles) {
            sb.put(p.x, p.y, p.z)
            sb.put(p.r, p.g, p.b)
        }
        sb.drawMode = gl1DrawMode
        val shader = gl1Shader!!
        shader.use()
        shader.m4x4("cameraMatrix", gl1CameraMatrix)
        shader.m4x4("modelMatrix", gl1ModelMatrix)
        sb.draw(shader)
        sb.destroy()
    }

    fun vertex(x: Float, y: Float) {
        val p = Point(x, y, 0f, gl1Color)
        gl1Triangles.add(p)
    }

    fun rotate(a: Float, x: Float, y: Float, z: Float) {
        gl1Matrix.rotate(a, x, y, z)
    }

    private val gl1Matrix
        get() = if (gl1MatrixMode == GL11.GL_PROJECTION)
            gl1CameraMatrix else gl1ModelMatrix


}