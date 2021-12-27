package org.lwjgl.opengl;

import static android.opengl.GLES10.GL_ALPHA_TEST;
import static android.opengl.GLES10.GL_BYTE;
import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_FALSE;
import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES10.GL_LINEAR_MIPMAP_NEAREST;
import static android.opengl.GLES10.GL_LINES;
import static android.opengl.GLES10.GL_LINE_LOOP;
import static android.opengl.GLES10.GL_LINE_STRIP;
import static android.opengl.GLES10.GL_LUMINANCE;
import static android.opengl.GLES10.GL_LUMINANCE_ALPHA;
import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES10.GL_NEAREST;
import static android.opengl.GLES10.GL_NEAREST_MIPMAP_LINEAR;
import static android.opengl.GLES10.GL_NEAREST_MIPMAP_NEAREST;
import static android.opengl.GLES10.GL_POINTS;
import static android.opengl.GLES10.GL_REPEAT;
import static android.opengl.GLES10.GL_RGB;
import static android.opengl.GLES10.GL_RGBA;
import static android.opengl.GLES10.GL_SHORT;
import static android.opengl.GLES10.GL_TEXTURE0;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.GL_TRIANGLE_FAN;
import static android.opengl.GLES10.GL_TRIANGLE_STRIP;
import static android.opengl.GLES10.GL_TRUE;
import static android.opengl.GLES10.GL_UNSIGNED_BYTE;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES11.GL_GENERATE_MIPMAP;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_INT;
import static android.opengl.GLES20.GL_NONE;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_STENCIL_ATTACHMENT;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES30.GL_COLOR_ATTACHMENT15;
import static android.opengl.GLES30.GL_DEPTH_COMPONENT32F;
import static android.opengl.GLES30.GL_DEPTH_STENCIL_ATTACHMENT;
import static android.opengl.GLES30.GL_PIXEL_PACK_BUFFER;
import static android.opengl.GLES30.GL_PIXEL_UNPACK_BUFFER;
import static android.opengl.GLES30.GL_R8;
import static android.opengl.GLES30.GL_RG;
import static android.opengl.GLES30.GL_RG8;
import static android.opengl.GLES30.GL_RGB16F;
import static android.opengl.GLES30.GL_RGB32F;
import static android.opengl.GLES30.GL_RGB8;
import static android.opengl.GLES30.GL_RGBA16F;
import static android.opengl.GLES30.GL_RGBA32F;
import static android.opengl.GLES30.GL_RGBA8;
import static android.opengl.GLES30.GL_TEXTURE_3D;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_R;
import static android.opengl.GLES31.GL_TEXTURE_2D_MULTISAMPLE;

import static javax.microedition.khronos.opengles.GL11ExtensionPack.GL_DEPTH_COMPONENT32;
import static javax.microedition.khronos.opengles.GL11ExtensionPack.GL_MIRRORED_REPEAT;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import me.anno.gpu.GFX;
import me.anno.gpu.buffer.Attribute;
import me.anno.gpu.buffer.StaticBuffer;
import me.anno.gpu.shader.Shader;
import me.anno.gpu.shader.builder.Variable;
import me.anno.gpu.texture.Texture2D;
import me.anno.utils.Warning;

public class GL11 {

    public static final int GL_COLOR_BUFFER_BIT = GLES11.GL_COLOR_BUFFER_BIT;
    public static final int GL_DEPTH_BUFFER_BIT = GLES11.GL_DEPTH_BUFFER_BIT;
    public static final int GL_STENCIL_BUFFER_BIT = GLES11.GL_STENCIL_BUFFER_BIT;

    public static final int GL_TRIANGLES = GLES11.GL_TRIANGLES;
    public static final int GL_QUADS = 7;// why???

    public static final int GL_DEBUG_OUTPUT = 0x92E0;// GLES32.GL_DEBUG_OUTPUT;

    public static final int GL_PROJECTION = GLES11.GL_PROJECTION;
    public static final int GL_MODELVIEW = GLES11.GL_MODELVIEW;

    // OpenGL is single-threaded, so we can use this static instance
    private static final int[] tmpInt1 = new int[1];

    public static int major, minor, version10x;

    public static void setVersion(int major, int minor) {
        GL11.major = major;
        GL11.minor = minor;
        version10x = major * 10 + minor;// 3.1 -> 31
    }

    static class Point {
        float x, y, z;
        float r, g, b;
    }

    private static final boolean print = true;

    private static int boundFramebuffer, boundProgram;

    private static final ArrayList<Point> gl1Triangles = new ArrayList<>();
    private static final ArrayList<Attribute> gl1Attributes = new ArrayList<>();
    private static final Matrix4f gl1ModelMatrix = new Matrix4f();
    private static final Matrix4f gl1CameraMatrix = new Matrix4f();
    private static final Shader gl1Shader;
    private static final Vector4f gl1Color = new Vector4f();
    private static int gl1MatrixMode;
    private static int gl1DrawMode;

    static {
        ArrayList<Variable> var = new ArrayList<>();
        var.add(new Variable("vec3", "color"));
        gl1Shader = new Shader(
                "OpenGL1",
                null, "" +
                "attribute vec3 pos;\n" +
                "attribute vec3 col;\n" +
                "uniform mat4 cameraMatrix, modelMatrix;\n" +
                "void main(){\n" +
                "   gl_Position = cameraMatrix * modelMatrix * vec4(pos, 1.0);\n" +
                "   color = col;\n" +
                "}", var, "" +
                "void main(){\n" +
                "   gl_FragColor = vec4(color, 1.0);\n" +
                "}", false
        );
        gl1Attributes.add(new Attribute("pos", 3));
        gl1Attributes.add(new Attribute("col", 3));
    }

    protected static void check(int mode) {
        int error = glGetError();
        if (error != 0)
            throw new RuntimeException("OpenGL returned error " + GFX.INSTANCE.getErrorTypeName(error) + " for mode " + mode);
    }

    protected static void check() {
        int error = glGetError();
        if (error != 0) {
            System.err.println("OpenGL returned error " + GFX.INSTANCE.getErrorTypeName(error));
            new RuntimeException("OpenGL returned error " + GFX.INSTANCE.getErrorTypeName(error) +
                    ", 0x" + Integer.toString(error, 16)).printStackTrace();
            System.exit(error);
        }
    }

    private static String getDrawMode(int mode) {
        switch (mode) {
            case GL_TRIANGLES:
                return "TRIANGLES";
            case GL_TRIANGLE_FAN:
                return "TRIANGLE_FAN";
            case GL_TRIANGLE_STRIP:
                return "TRIANGLE_STRIP";
            case GL_QUADS:
                return "QUADS";
            case GL_LINES:
                return "LINES";
            case GL_LINE_STRIP:
                return "LINE_STRIP";
            default:
                return mode + "";
        }
    }

    private static String getTextureTarget(int target) {
        switch (target) {
            case GL_TEXTURE_2D:
                return "TEXTURE_2D";
            case GL_TEXTURE_2D_MULTISAMPLE:
                return "TEXTURE_2D_MULTISAMPLE";
            case GL_TEXTURE_3D:
                return "TEXTURE_3D";
            case GL_TEXTURE_CUBE_MAP:
                return "TEXTURE_CUBE_MAP";
            case GL_FRAMEBUFFER:
                return "FRAMEBUFFER";
            default:
                return target + "";
        }
    }

    private static String getAttachment(int attachment) {
        if (attachment >= GL_COLOR_ATTACHMENT0 && attachment <= GL_COLOR_ATTACHMENT15) {
            return "COLOR_ATTACHMENT" + (attachment - GL_COLOR_ATTACHMENT0);
        }
        switch (attachment) {
            case GL_NONE:
                return "NONE";
            case GL_DEPTH_ATTACHMENT:
                return "DEPTH_ATTACHMENT";
            case GL_STENCIL_ATTACHMENT:
                return "STENCIL_ATTACHMENT";
            case GL_DEPTH_STENCIL_ATTACHMENT:
                return "DEPTH_STENCIL_ATTACHMENT";
            default:
                return attachment + "";
        }
    }

    private static String getBufferTarget(int target) {
        switch (target) {
            case GL_ARRAY_BUFFER:
                return "ARRAY_BUFFER";
            case GL_ELEMENT_ARRAY_BUFFER:
                return "ELEMENT_ARRAY_BUFFER";
            case GL_PIXEL_PACK_BUFFER:
                return "PIXEL_PACK_BUFFER";
            case GL_PIXEL_UNPACK_BUFFER:
                return "PIXEL_UNPACK_BUFFER";
            default:
                return target + "";
        }
    }

    private static String getBufferUsage(int target) {
        switch (target) {
            case GL_STATIC_DRAW:
                return "STATIC_DRAW";
            case GL_DYNAMIC_DRAW:
                return "DYNAMIC_DRAW";
            case GL_STREAM_DRAW:
                return "STREAM_DRAW";
            default:
                return target + "";
        }
    }

    private static String getType(int type) {
        switch (type) {
            case GL_UNSIGNED_BYTE:
                return "UNSIGNED_BYTE";
            case GL_UNSIGNED_SHORT:
                return "UNSIGNED_SHORT";
            case GL_UNSIGNED_INT:
                return "UNSIGNED_INT";
            case GL_FLOAT:
                return "FLOAT";
            case GL_BYTE:
                return "BYTE";
            case GL_SHORT:
                return "SHORT";
            case GL_INT:
                return "INT";
            default:
                return type + "";
        }
    }

    private static String getFormat(int format) {
        switch (format) {
            case GL_LUMINANCE:
                return "LUMINANCE";
            case GL_LUMINANCE_ALPHA:
                return "LUMINANCE_ALPHA";
            case GL_R8:
                return "R8";
            case GL_RG:
                return "RG";
            case GL_RG8:
                return "RG8";
            case GL_RGB:
                return "RGB";
            case GL_RGB8:
                return "RGB8";
            case GL_RGBA:
                return "RGBA";
            case GL_RGBA8:
                return "RGBA8";
            case GL_RGB16F:
                return "RGB16F";
            case GL_RGBA16F:
                return "RGBA16F";
            case GL_RGB32F:
                return "RGB32F";
            case GL_RGBA32F:
                return "RGBA32F";
            case GL_DEPTH_COMPONENT:
                return "DEPTH_COMPONENT";
            case GL_DEPTH_COMPONENT16:
                return "DEPTH_COMPONENT16";
            case GL_DEPTH_COMPONENT32:
                return "DEPTH_COMPONENT32";
            case GL_DEPTH_COMPONENT32F:
                return "DEPTH_COMPONENT32F";
            default:
                return format + "";
        }
    }

    public static int glslVersion = 0;
    private static String glslVersionString;

    public static void testShaderVersions() {
        check();
        int maxVersion = 0;
        for (int version = 100; version < 400; version += 10) {
            int shader = GLES20.glCreateShader(GL_VERTEX_SHADER);
            GLES20.glShaderSource(shader, "" +
                    "#version " + version + " es\n" +
                    "void main(){ gl_Position = vec4(1.0); }");
            GLES20.glCompileShader(shader);
            String log = glGetShaderInfoLog(shader);
            if (log == null || log.trim().isEmpty()) {
                maxVersion = version;
            }
            GLES20.glDeleteShader(shader);
        }
        while (GLES20.glGetError() != 0) ;// absorb errors
        if (print) System.out.println("Maximum supported GLSL version: " + maxVersion);
        glslVersion = maxVersion;
        glslVersionString = "#version " + glslVersion + " es\n";
        check();
    }

    public static void glClearColor(float r, float g, float b, float a) {
        check();
        GLES11.glClearColor(r, g, b, a);
        check();
    }

    public static void glClear(int mask) {
        check();
        GLES11.glClear(mask);
        check();
    }

    public static void glMatrixMode(int mode) {
        gl1MatrixMode = mode;
    }

    private static Matrix4f getGL1Matrix() {
        return gl1MatrixMode == GL_PROJECTION ? gl1CameraMatrix : gl1ModelMatrix;
    }

    public static void glLoadIdentity() {
        getGL1Matrix().identity();
    }

    public static void glOrtho(float x0, float x1, float y0, float y1, float z0, float z1) {
        getGL1Matrix().ortho(x0, x1, y0, y1, z0, z1);
    }

    public static void glOrtho(double x0, double x1, double y0, double y1, double z0, double z1) {
        glOrtho((float) x0, (float) x1, (float) y0, (float) y1, (float) z0, (float) z1);
    }

    public static void glColor3f(float r, float g, float b) {
        gl1Color.set(r, g, b);
    }

    public static void glBegin(int mode) {
        gl1Triangles.clear();
        gl1DrawMode = mode;
    }

    public static void glEnd() {
        // create a new buffer
        StaticBuffer sb = new StaticBuffer(gl1Attributes, gl1Triangles.size(), GL_STATIC_DRAW);
        for (Point p : gl1Triangles) {
            sb.put(p.x, p.y, p.z);
            sb.put(p.r, p.g, p.b);
        }
        sb.setDrawMode(gl1DrawMode);
        Shader shader = gl1Shader;
        shader.use();
        shader.m4x4("cameraMatrix", gl1CameraMatrix);
        shader.m4x4("modelMatrix", gl1ModelMatrix);
        sb.draw(shader);
        sb.destroy();
    }

    public static void glVertex2f(float x, float y) {
        Point p = new Point();
        p.x = x;
        p.y = y;
        p.z = 0f;
        p.r = gl1Color.x;
        p.g = gl1Color.y;
        p.b = gl1Color.z;
        gl1Triangles.add(p);
    }

    public static void glRotatef(float a, float x, float y, float z) {
        getGL1Matrix().rotate(a, x, y, z);
    }

    public static void glEnable(int flags) {
        if (flags == GL_ALPHA_TEST || flags == GL_MULTISAMPLE) return;
        check();
        GLES11.glEnable(flags);
        check();
    }

    public static void glDisable(int flags) {
        // why ever, these are no flags in OpenGL ES
        if (flags == GL_ALPHA_TEST || flags == GL_MULTISAMPLE) return;
        check();
        GLES11.glDisable(flags);
        check(flags);
    }

    public static void glCullFace(int face) {
        GLES20.glCullFace(face);
    }

    public static void glPixelStorei(int key, int value) {
        check();
        GLES11.glPixelStorei(key, value);
        if (print) System.out.println("glPixelStorei(" + key + ", " + value + ")");
        check();
    }

    public static int glGetInteger(int i) {
        check();
        GLES11.glGetIntegerv(i, tmpInt1, 0);
        if (print) System.out.println("glGetInteger(" + i + ")");
        check();
        return tmpInt1[0];
    }

    public static int glGetError() {
        return GLES11.glGetError();
    }

    public static void glGenTextures(int[] tex) {
        check();
        GLES11.glGenTextures(tex.length, tex, 0);
        if (print) System.out.println("glGenTextures() -> " + Arrays.toString(tex));
        check();
    }

    public static void glActiveTexture(int index) {
        check();
        GLES11.glActiveTexture(index);
        if (print) System.out.println("glActiveTexture(" + (index - GL_TEXTURE0) + ")");
        check();
    }

    public static void glBindTexture(int target, int pointer) {
        check();
        GLES11.glBindTexture(target, pointer);
        if (print)
            System.out.println("glBindTexture(" + getTextureTarget(target) + ", " + pointer + ")");
        check();
    }

    public static void glBindBuffer(int target, int pointer) {
        check();
        GLES11.glBindBuffer(target, pointer);
        if (print)
            System.out.println("glBindBuffer(" + getBufferTarget(target) + ", " + pointer + ")");
        check();
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer buffer) {
        check();
        if (internalFormat == GL_LUMINANCE) format = internalFormat;// OpenGL ES is stricter
        GLES11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer);
        if (print)
            System.out.println("glTexImage2D(" + getTextureTarget(target) + ", level " + level +
                    ", internal " + getFormat(internalFormat) + ", " + width + " x " + height + ", " + border +
                    ", format " + getFormat(format) + ", " + getType(type) +
                    ", data: " + (buffer != null ? "x" + buffer.remaining() : "*0"));
        check();
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, long dataPointer) {
        if (dataPointer != 0) throw new RuntimeException("Expected data pointer to be null");
        check();
        if (internalFormat == GL_DEPTH_COMPONENT16) {// OpenGL ES is stricter
            format = GL_DEPTH_COMPONENT;
            type = GL_UNSIGNED_INT;
        }
        GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, null);
        if (print)
            System.out.println("glTexImage2D(" + getTextureTarget(target) + ", level " + level +
                    ", internal " + getFormat(internalFormat) + ", " + width + " x " + height + ", " + border +
                    ", format " + getFormat(format) + ", " + getType(type) + ", data: *" + dataPointer);
        check();
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, int[] data) {
        check();
        ByteBuffer buffer0 = Texture2D.Companion.getBufferPool().get(data.length * 4, false);
        IntBuffer buffer = buffer0.asIntBuffer(); // IntBuffer.allocate(data.length);
        if (internalFormat == GL_RGB8 && format == GL_RGBA) {
            for (int argb : data) {
                buffer0.put((byte) ((argb >> 16) & 255));
                buffer0.put((byte) ((argb >> 8) & 255));
                buffer0.put((byte) ((argb) & 255));
            }
            format = GL_RGB;// OpenGL ES is more strict than OpenGL
            buffer0.rewind();
            GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer0);
        } else {
            buffer.put(data);
            buffer.rewind();
            GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer);
        }
        if (print)
            System.out.println("glTexImage2D(" + getTextureTarget(target) + ", level " + level +
                    ", internal " + getFormat(internalFormat) + ", " + width + " x " + height + ", " + border +
                    ", format " + getFormat(format) + ", " + getType(type) + ", data[int]: x" + data.length);
        Texture2D.Companion.getBufferPool().returnBuffer(buffer0);
        check();
    }

    public static void glTexSubImage2D(int target, int level, int x, int y, int w, int h, int format, int type, ByteBuffer data) {
        check();
        GLES20.glTexSubImage2D(target, level, x, y, w, h, format, type, data);
        if (print)
            System.out.println("glTexSubImage2D(" + getTextureTarget(target) + ", level " + level + ", " + x +
                    ", " + y + ", " + w + ", " + h + ", " + getFormat(format) +
                    ", " + getType(type) + ", " + data + ")");
        check();
    }

    private static String getTexKey(int key) {
        switch (key) {
            case GL_TEXTURE_MAG_FILTER:
                return "TEXTURE_MAG_FILTER";
            case GL_TEXTURE_MIN_FILTER:
                return "TEXTURE_MIN_FILTER";
            case GL_TEXTURE_WRAP_S:
                return "TEXTURE_WRAP_S";
            case GL_TEXTURE_WRAP_T:
                return "TEXTURE_WRAP_T";
            case GL_TEXTURE_WRAP_R:
                return "TEXTURE_WRAP_R";
            case GL_GENERATE_MIPMAP:
                return "GENERATE_MIPMAP";
            default:
                return key + "";
        }
    }

    private static String getTexValue(int value) {
        switch (value) {
            case GL_FALSE:
                return "FALSE";
            case GL_TRUE:
                return "TRUE";
            case GL_LINEAR:
                return "LINEAR";
            case GL_NEAREST:
                return "NEAREST";
            case GL_CLAMP_TO_EDGE:
                return "CLAMP_TO_EDGE";
            case GL_REPEAT:
                return "REPEAT";
            case GL_MIRRORED_REPEAT:
                return "MIRRORED_REPEAT";
            case GL_LINEAR_MIPMAP_LINEAR:
                return "LINEAR_MIPMAP_LINEAR";
            case GL_LINEAR_MIPMAP_NEAREST:
                return "LINEAR_MIPMAP_NEAREST";
            case GL_NEAREST_MIPMAP_LINEAR:
                return "NEAREST_MIPMAP_LINEAR";
            case GL_NEAREST_MIPMAP_NEAREST:
                return "NEAREST_MIPMAP_NEAREST";
            default:
                return value + "";
        }
    }

    public static void glTexParameteri(int target, int key, int value) {
        check();
        if (print)
            System.out.println("glTexParameteri(" + getTextureTarget(target) + ", " + getTexKey(key) + ", " + getTexValue(value) + ")");
        if (key == GL_GENERATE_MIPMAP) {
            // only supported in OpenGL ES 1.1, not 2.0+???
            return;
        }
        GLES11.glTexParameteri(target, key, value);
        check();
    }

    public static void glBlendEquationSeparate(int i, int j) {
        check();
        GLES20.glBlendEquationSeparate(i, j);
        check();
    }

    public static void glBlendFuncSeparate(int i, int j, int k, int l) {
        check();
        GLES20.glBlendFuncSeparate(i, j, k, l);
        check();
    }

    public static void glBindFramebuffer(int target, int pointer) {
        check();
        GLES20.glBindFramebuffer(target, pointer);
        if (print)
            System.out.println("glBindFramebuffer(" + getTextureTarget(target) + ", " + pointer + ")");
        check();
        boundFramebuffer = pointer;
    }

    public static void glViewport(int x, int y, int w, int h) {
        check();
        GLES11.glViewport(x, y, w, h);
        if (print) System.out.println("glViewport(" + x + ", " + y + ", " + w + ", " + h + ")");
        check();
    }

    public static int glGenFramebuffers() {
        check();
        GLES20.glGenFramebuffers(1, tmpInt1, 0);
        if (print) System.out.println("glGenFramebuffers() -> " + tmpInt1[0]);
        check();
        return tmpInt1[0];
    }

    public static void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        check();
        GLES20.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
        if (print)
            System.out.println("glFramebufferTexture2D(" + getTextureTarget(target) + ", " + getAttachment(attachment) + ", " +
                    getTextureTarget(textureTarget) + ", " + texture + ", " + level + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDrawBuffer(int buffer) {
        tmpInt1[0] = buffer;
        check();
        GLES30.glDrawBuffers(1, tmpInt1, 0);
        if (print) System.out.println("glDrawBuffers(" + getAttachment(buffer) + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDrawBuffers(int[] buffers) {
        check();
        GLES30.glDrawBuffers(buffers.length, buffers, 0);
        if (print) System.out.println("glDrawBuffers(" + Arrays.toString(buffers) + ")");
        check();
    }

    public static int glCheckFramebufferStatus(int target) {
        check();
        int status = GLES20.glCheckFramebufferStatus(target);
        if (print)
            System.out.println("glCheckFramebufferStatus(" + getTextureTarget(target) + ") -> " + status);
        check();
        return status;
    }

    public static int glCheckNamedFramebufferStatus(int pointer, int target) {
        check();
        // named version is not available in OpenGL ES
        if (pointer != boundFramebuffer) GLES20.glBindFramebuffer(target, pointer);
        int status = GLES20.glCheckFramebufferStatus(target);
        if (pointer != boundFramebuffer) GLES20.glBindFramebuffer(target, boundFramebuffer);
        if (print)
            System.out.println("glCheckNamedFramebufferStatus(" + pointer + ", " + getTextureTarget(target) + ") -> " + status);
        check();
        return status;
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        tmpInt1[0] = framebuffer;
        check();
        GLES20.glDeleteFramebuffers(1, tmpInt1, 0);
        if (print) System.out.println("glDeleteFramebuffers(" + framebuffer + ")");
        check();
    }

    public static int glCreateProgram() {
        int program = GLES20.glCreateProgram();
        if (print) System.out.println("glCreateProgram() -> " + program);
        return program;
    }

    public static int glCreateShader(int type) {
        return GLES20.glCreateShader(type);
    }

    public static void glShaderSource(int shader, CharSequence source) {
        check();
        String str = source.toString()
                .replace(" \n", "\n")
                // only 320 is supported on my Huawei H10, at least in my testing
                .replace("#version 150 es", glslVersionString)
                .replace("#version 160 es", glslVersionString)
                .replace("#version 170 es", glslVersionString)
                .replace("#version 180 es", glslVersionString)
                .replace("#version 190 es", glslVersionString)
                .replace("#version 300 es", glslVersionString)
                .replace("#version 310 es", glslVersionString)
                .replace("#version 320 es", glslVersionString)
                .replace("#version 330 es", glslVersionString)
                .replace("#version 340 es", glslVersionString)
                .replace("#version 350 es", glslVersionString)
                .replace("#version 360 es", glslVersionString)
                .replace("#version 370 es", glslVersionString)
                .replace("#version 400 es", glslVersionString)
                .replace("#version 410 es", glslVersionString)
                .replace("#version 420 es", glslVersionString)
                .replace("attribute ", "in "); // mmh, changed like this in OpenGL ES
        GLES20.glShaderSource(shader, str);
        String log = glGetShaderInfoLog(shader);
        if (log != null && !log.trim().isEmpty()) {
            if (print) System.out.println("Shader " + str.replace("\n", "\\n") + " caused " + log);
        }
        int nameStartIndex = str.indexOf("//");
        int nameEndIndex = str.indexOf('\n', nameStartIndex + 1);
        if (nameEndIndex > nameStartIndex) {
            if (print)
                System.out.println("glShaderSource(" + shader + ", ...) by " + str.substring(nameStartIndex, nameEndIndex).trim());
        } else {
            if (print) System.out.println("glShaderSource(" + shader + ", ...)");
        }
        check();
    }

    public static void glCompileShader(int shader) {
        check();
        GLES20.glCompileShader(shader);
        check();
    }

    public static void glAttachShader(int program, int shader) {
        check();
        GLES20.glAttachShader(program, shader);
        check();
    }

    public static String glGetShaderInfoLog(int shader) {
        return GLES20.glGetShaderInfoLog(shader);
    }

    public static String glGetProgramInfoLog(int shader) {
        return GLES20.glGetProgramInfoLog(shader);
    }

    public static void glLinkProgram(int program) {
        GLES20.glLinkProgram(program);
    }

    public static void glDeleteShader(int shader) {
        check();
        GLES20.glDeleteShader(shader);
        check();
    }

    public static void glUseProgram(int program) {
        check();
        GLES20.glUseProgram(program);
        if (print) System.out.println("glUseProgram(" + program + ")");
        boundProgram = program;
        check();
    }

    public static int glGetUniformLocation(int program, CharSequence name) {
        check();
        int loc = GLES20.glGetUniformLocation(program, name.toString());
        if (print)
            System.out.println("glGetUniformLocation(" + program + ", " + name + ") -> " + loc);
        check();
        return loc;
    }

    public static void glUniform1i(int uniform, int x) {
        check();
        GLES20.glUniform1i(uniform, x);
        if (print) System.out.println("glUniform1i(" + uniform + ", " + x + ")");
        check();
    }

    public static void glUniform1f(int uniform, float x) {
        check();
        GLES20.glUniform1f(uniform, x);
        if (print) System.out.println("glUniform1f(" + uniform + ", " + x + ")");
        check();
    }

    public static void glUniform2f(int uniform, float x, float y) {
        check();
        GLES20.glUniform2f(uniform, x, y);
        if (print) System.out.println("glUniform2f(" + uniform + ", " + x + ", " + y + ")");
        check();
    }

    public static void glUniform3f(int uniform, float x, float y, float z) {
        check();
        GLES20.glUniform3f(uniform, x, y, z);
        if (print)
            System.out.println("glUniform3f(" + uniform + ", " + x + ", " + y + ", " + z + ")");
        check();
    }

    public static void glUniform3fv(int uniform, FloatBuffer data) {
        check();
        GLES20.glUniform3fv(uniform, data.remaining() / 3, data);
        if (print)
            System.out.println("glUniform3fv(" + uniform + ", " + data.remaining() / 3 + ", " + data + ")");
        check();
    }

    public static void glUniform4f(int uniform, float x, float y, float z, float w) {
        check();
        GLES20.glUniform4f(uniform, x, y, z, w);
        if (print)
            System.out.println("glUniform4f(" + uniform + ", " + x + ", " + y + ", " + z + ", " + w + ")");
        check();
    }

    public static void glUniform4fv(int uniform, FloatBuffer buffer) {
        check();
        GLES20.glUniform4fv(uniform, buffer.remaining() / 4, buffer);
        if (print) System.out.println("glUniform4fv(" + uniform + ", " + buffer + ")");
        check();
    }

    public static void glUniformMatrix4fv(int uniform, boolean rowMajor, FloatBuffer buffer) {
        check();
        GLES20.glUniformMatrix4fv(uniform, buffer.remaining() / 16, rowMajor, buffer);
        if (print)
            System.out.println("glUniformMatrix4fv(" + uniform + ", " + buffer.remaining() / 16 +
                    ", " + rowMajor + ", " + buffer + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glUniformMatrix4x3fv(int uniform, boolean rowMajor, FloatBuffer buffer) {
        check();
        GLES30.glUniformMatrix4x3fv(uniform, buffer.remaining() / 12, rowMajor, buffer);
        if (print)
            System.out.println("glUniformMatrix4x3fv(" + uniform + ", " + buffer.remaining() / 12 +
                    ", " + rowMajor + ", " + buffer + ")");
        check();
    }

    public static int glGetAttribLocation(int program, CharSequence name) {
        check();
        int loc = GLES20.glGetAttribLocation(program, name.toString());
        if (print)
            System.out.println("glGetAttribLocation(" + program + ", " + name + ") -> " + loc);
        check();
        return loc;
    }

    public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
        check();
        GLES20.glVertexAttribPointer(index, size, type, normalized, stride, (int) offset);
        if (print)
            System.out.println("glVertexAttribPointer(" + index + ", " + size + ", " + getType(type) + ", " + normalized + ", " + stride + ", " + offset + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glVertexAttribDivisor(int index, int divisor) {
        check();
        GLES30.glVertexAttribDivisor(index, divisor);
        if (print) System.out.println("glVertexAttribDivisor(" + index + ", " + divisor + ")");
        check();
    }

    public static void glEnableVertexAttribArray(int index) {
        check();
        GLES20.glEnableVertexAttribArray(index);
        if (print) System.out.println("glEnableVertexAttribArray(" + index + ")");
        check();
    }

    public static void glDisableVertexAttribArray(int index) {
        check();
        GLES20.glDisableVertexAttribArray(index);
        if (print) System.out.println("glDisableVertexAttribArray(" + index + ")");
        check();
    }

    public static int glGenBuffers() {
        check();
        GLES20.glGenBuffers(1, tmpInt1, 0);
        if (print) System.out.println("glGenBuffer() -> " + tmpInt1[0]);
        check();
        return tmpInt1[0];
    }

    public static void glDepthRange(double near, double far) {
        check();
        GLES20.glDepthRangef((float) near, (float) far);
        if (print) System.out.println("glDepthRange(" + near + ", " + far + ")");
        check();
    }

    public static void glDeleteBuffers(int i) {
        check();
        tmpInt1[0] = i;
        GLES20.glDeleteBuffers(1, tmpInt1, 0);
        if (print) System.out.println("glDeleteBuffers(" + i + ")");
        check();
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        // correct?
        check();
        GLES20.glBufferData(target, data.remaining(), data, usage);
        if (print)
            System.out.println("glBufferData(" + getBufferTarget(target) + ", " + data + ", " + getBufferUsage(usage) + ")");
        check();
    }

    public static void glBufferData(int target, ShortBuffer data, int usage) {
        check();
        GLES20.glBufferData(target, data.remaining() * 2, data, usage);
        if (print)
            System.out.println("glBufferData(" + getBufferTarget(target) + ", " + data + ", " + getBufferUsage(usage) + ")");
        check();
    }

    public static void glBufferSubData(int target, long offset, ByteBuffer buffer) {
        if (offset > Integer.MAX_VALUE) throw new RuntimeException("Max allowed offset is 2GBi");
        check();
        // check the memory is valid
        buffer.get(0);
        buffer.get(buffer.limit() - 1);
        if (print)
            System.out.println("glBufferSubData(" + getBufferTarget(target) + ", " + offset + ", " + buffer + ")");
        GLES20.glBufferSubData(target, (int) offset, buffer.remaining(), buffer);
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int glGenVertexArrays() {
        check();
        GLES30.glGenVertexArrays(1, tmpInt1, 0);
        if (print) System.out.println("glGenVertexArrays() -> " + tmpInt1[0]);
        check();
        return tmpInt1[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDeleteVertexArrays(int i) {
        check();
        tmpInt1[0] = i;
        GLES30.glDeleteVertexArrays(1, tmpInt1, 0);
        if (print) System.out.println("glDeleteVertexArrays(" + i + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glBindVertexArray(int array) {
        check();
        GLES30.glBindVertexArray(array);
        if (print) System.out.println("glBindVertexArray(" + array + ")");
        check();
    }

    public static void glDrawArrays(int mode, int first, int count) {
        check();
        checkProgramStatus();
        GLES20.glDrawArrays(mode, first, count);
        if (print)
            System.out.println("glDrawArrays(" + getDrawMode(mode) + ", " + first + ", " + count + ")");
        absorbErrors();
        // check();
    }

    public static void glDrawElements(int mode, int count, int type, int offset) {
        check();
        checkProgramStatus();
        GLES20.glDrawElements(mode, count, type, offset);
        if (print)
            System.out.println("glDrawElements(" + getDrawMode(mode) + ", " + count + "x, " + getType(type) + ", " + offset + ")");
        absorbErrors();
        // check();
    }

    public static void glDrawElements(int mode, int count, int type, long data) {
        if (data != 0) throw new RuntimeException("Expected data pointer to be null");
        check();
        checkProgramStatus();
        GLES20.glDrawElements(mode, count, type, 0);
        if (print)
            System.out.println("glDrawElements(" + getDrawMode(mode) + ", " + count + "x, " + getType(type) + ")");
        absorbErrors();
        // check();
    }

    private static void checkProgramStatus(){
        // for testing
        if (print) {
            check();
            GLES30.glValidateProgram(boundProgram);
            GLES30.glGetProgramiv(boundProgram, GL_VALIDATE_STATUS, tmpInt1, 0);
            int validationStatus = tmpInt1[0];// true = validation success
            String log = glGetProgramInfoLog(boundProgram);
            // todo there will be a failure, if a texture is bound to 2 slots, and they use different samplers
            System.out.println("status: " + validationStatus + ", log: \"" + log + "\"");
            check();
        }
    }

    private static void absorbErrors(){
        glGetError();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDrawElementsInstanced(int mode, int count, int type, long firstInstanceIndex, int instanceCount) {

        if (firstInstanceIndex > Integer.MAX_VALUE)
            throw new RuntimeException("First instance index must be less than 2e9");

        checkProgramStatus();

        // crashes the same way
        // GLES30.glDrawElements(mode, count, type, 0);
        check();
        System.out.println("glDrawElementsInstanced(" + getDrawMode(mode) + ", " + count + "x, " + getType(type) + ", 0, " + instanceCount + ")");
        GLES30.glDrawElementsInstanced(mode, count, type, (int) firstInstanceIndex, instanceCount);
        // check();
        absorbErrors();

    }

    public static void glDepthFunc(int func) {
        check();
        GLES20.glDepthFunc(func);
        check();
    }

    public static void glClearDepth(double depth) {
        check();
        GLES20.glClearDepthf((float) depth);
        check();
    }

    public static void glClipControl(int origin, int depth) {
        Warning.INSTANCE.warn("Cannot call glClipControl on Android!");
    }

    public static void glDeleteTextures(int[] textures) {
        check();
        GLES20.glDeleteTextures(textures.length, textures, 0);
        check();
    }

    public static void glDepthMask(boolean doWriteDepth) {
        check();
        GLES20.glDepthMask(doWriteDepth);
        if (print) System.out.println("glDepthMask(" + doWriteDepth + ")");
        check();
    }

    public static void glGenerateMipmap(int target) {
        check();
        GLES20.glGenerateMipmap(target);
        if (print) System.out.println("glGenerateMipmap(" + target + ")");
        check();
    }

    public static void glFlush() {
        check();
        GLES20.glFlush();
        if (print) System.out.println("glFlush()");
        check();
    }

    public static void glFinish() {
        check();
        GLES20.glFinish();
        if (print) System.out.println("glFinish()");
        check();
    }

    public static void glReadPixels(int x, int y, int w, int h, int format, int type, ByteBuffer pixels) {
        check();
        GLES20.glReadPixels(x, y, w, h, format, type, pixels);
        if (print) System.out.println("glReadPixels(" + x + ", " + y + ", " + w + ", " + h +
                ", " + getFormat(format) + ", " + getType(type) + ", " + pixels + ")");
        check();
    }

}
