package org.lwjgl.opengl;

import static android.opengl.GLES10.GL_ALPHA_TEST;
import static android.opengl.GLES10.GL_LUMINANCE;
import static android.opengl.GLES10.GL_MULTISAMPLE;
import static android.opengl.GLES10.GL_RGB;
import static android.opengl.GLES10.GL_RGBA;
import static android.opengl.GLES10.GL_TEXTURE0;
import static android.opengl.GLES11.GL_GENERATE_MIPMAP;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES30.GL_RGB8;

import static org.lwjgl.opengl.GLStrings.getAttachment;
import static org.lwjgl.opengl.GLStrings.getBufferTarget;
import static org.lwjgl.opengl.GLStrings.getBufferUsage;
import static org.lwjgl.opengl.GLStrings.getDrawMode;
import static org.lwjgl.opengl.GLStrings.getFormat;
import static org.lwjgl.opengl.GLStrings.getTexKey;
import static org.lwjgl.opengl.GLStrings.getTexValue;
import static org.lwjgl.opengl.GLStrings.getTextureTarget;
import static org.lwjgl.opengl.GLStrings.getType;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.opengl.GLES32;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import me.anno.OpenGLLegacy;
import me.anno.gpu.GFX;
import me.anno.gpu.texture.Texture2D;
import me.anno.utils.Warning;

@SuppressWarnings("unused")
public class GL11 {

    private static final boolean print = false;
    private static final boolean printFramebuffers = false;
    private static final boolean printDeletes = false;
    private static final boolean printTexBinds = false;
    private static final boolean ignoreErrors = true;
    private static final int testVersionBecauseAndroidStudioBuggy = 0;

    public static final int GL_COLOR_BUFFER_BIT = GLES11.GL_COLOR_BUFFER_BIT;
    public static final int GL_DEPTH_BUFFER_BIT = GLES11.GL_DEPTH_BUFFER_BIT;
    public static final int GL_STENCIL_BUFFER_BIT = GLES11.GL_STENCIL_BUFFER_BIT;

    public static final int GL_TRIANGLES = GLES11.GL_TRIANGLES;
    public static final int GL_QUADS = 7;// why is this missing for OpenGL ES? legacy, even in normal OpenGL

    public static final int GL_DEBUG_OUTPUT = 0x92E0;
    public static final int GL_TEXTURE_SWIZZLE_RGBA = 36422;// since OpenGL ES 3.3, which somehow isn't part of Android...

    public static final int GL_PROJECTION = GLES11.GL_PROJECTION;
    public static final int GL_MODELVIEW = GLES11.GL_MODELVIEW;

    // OpenGL is single-threaded, so we can use this static instance
    private static final int[] tmpInt1 = new int[1];
    private static final int[] createdFramebuffers = new int[65536];
    private static final int[] createdTextures = new int[65536];
    private static final int[] boundTextures = new int[64];
    private static int activeTexture = 0;
    private static int boundFramebuffer, boundProgram;

    public static int major, minor, version10x;
    public static int glslVersion = 0;

    private static String glslVersionString;

    public static void setVersion(int major, int minor) {
        GL11.major = major;
        GL11.minor = minor;
        version10x = major * 10 + minor;// 3.1 -> 31
    }

    public static void invalidateBinding() {
        Arrays.fill(createdFramebuffers, 0);
        Arrays.fill(createdTextures, 0);
        Arrays.fill(boundTextures, 0);
        if (printTexBinds) System.out.println("Cleared all bindings");
        activeTexture = 0;
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
        // absorb errors
        for (int errorCtr = 0; errorCtr < 100; errorCtr++) {
            if (GLES20.glGetError() == 0) break;
        }
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
        OpenGLLegacy.INSTANCE.glMatrixMode(mode);
    }

    public static void glLoadIdentity() {
        OpenGLLegacy.INSTANCE.loadIdentity();
    }

    public static void glOrtho(float x0, float x1, float y0, float y1, float z0, float z1) {
        OpenGLLegacy.INSTANCE.ortho(x0, x1, y0, y1, z0, z1);
    }

    public static void glOrtho(double x0, double x1, double y0, double y1, double z0, double z1) {
        glOrtho((float) x0, (float) x1, (float) y0, (float) y1, (float) z0, (float) z1);
    }

    public static void glColor3f(float r, float g, float b) {
        OpenGLLegacy.INSTANCE.color(r, g, b);
    }

    public static void glBegin(int mode) {
        OpenGLLegacy.INSTANCE.begin(mode);
    }

    public static void glEnd() {
        OpenGLLegacy.INSTANCE.end();
    }

    public static void glVertex2f(float x, float y) {
        OpenGLLegacy.INSTANCE.vertex(x, y);
    }

    public static void glRotatef(float a, float x, float y, float z) {
        OpenGLLegacy.INSTANCE.rotate(a, x, y, z);
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
        return ignoreErrors ? 0 : GLES11.glGetError();
    }

    public static void glGenTextures(int[] tex) {
        check();
        GLES11.glGenTextures(tex.length, tex, 0);
        if (print) System.out.println("glGenTextures() -> " + Arrays.toString(tex));
        for (int i : tex) {
            if (i < createdTextures.length) {
                if (createdTextures[i] == i)
                    throw new RuntimeException("glGenTextures returned " + i + " twice");
                createdTextures[i] = i;
            }
        }
        check();
    }

    public static void glActiveTexture(int index) {
        check();
        activeTexture = index - GL_TEXTURE0;
        GLES11.glActiveTexture(index);
        if (print) System.out.println("glActiveTexture(" + (index - GL_TEXTURE0) + ")");
        check();
    }

    public static void glBindTexture(int target, int pointer) {
        check();
        if (pointer < createdTextures.length && createdTextures[pointer] != pointer) {
            // may be called after a new session; idk, may be an engine bug
            new RuntimeException("Cannot bind undefined texture").printStackTrace();
            return;
        }
        GLES11.glBindTexture(target, pointer);
        if (print || printTexBinds)
            System.out.println("glBindTexture[" + activeTexture + "](" + getTextureTarget(target) + ", " + pointer + ")");
        boundTextures[activeTexture] = pointer;
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
        if (buffer == null || buffer.remaining() == 0) {
            glTexImage2D(target, level, internalFormat, width, height, border, format, type, 0L);
        } else {
            check();
            if (internalFormat == GL_LUMINANCE) format = internalFormat;// OpenGL ES is stricter
            GLES11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer);
            if (print)
                System.out.println("glTexImage2D(" + getTextureTarget(target) + ", level " + level +
                        ", internal " + getFormat(internalFormat) + ", " + width + " x " + height + ", " + border +
                        ", format " + getFormat(format) + ", " + getType(type) +
                        ", data: " + buffer);
            check();
        }
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

    public static void glTexImage2DMultisample(int target, int samples, int internalFormat, int width, int height, boolean fixedSamplePositions) {
        if (Build.VERSION.SDK_INT >= 21) {
            check();
            GLES31.glTexStorage2DMultisample(target, samples, internalFormat, width, height, fixedSamplePositions);
            if (print)
                System.out.println("glTexImage2DMultisample(" + getTextureTarget(target) +
                        ", internal " + getFormat(internalFormat) + ", " + width + " x " + height + ", fixed?: " + fixedSamplePositions);
            check();
        } else
            throw new RuntimeException("Operation glTexImage2DMultisample is not supported in Android API " + Build.VERSION.SDK_INT + ", min 21 is required");
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
            Texture2D.Companion.writeAlignment(width * 3);
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
        if (print || glGetError() != 0)
            System.out.println("glTexSubImage2D(" + getTextureTarget(target) + ", level " + level + ", " + x +
                    ", " + y + ", " + w + ", " + h + ", " + getFormat(format) +
                    ", " + getType(type) + ", " + data + ")");
        check();
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

    public static void glTexParameteriv(int target, int key, int[] values) {
        if (print)
            System.out.println("glTexParameteriv(" + getTextureTarget(target) + ", " + getTexKey(key) + ", " + Arrays.toString(values) + ")");
        if (key == GL_TEXTURE_SWIZZLE_RGBA && version10x <= 32) {
            // only supported in OpenGL ES 3.3
            return;
        }
        check();
        GLES11.glTexParameteriv(target, key, values, 0);
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
        if (pointer < 0)
            throw new RuntimeException("Cannot bind fb " + pointer);
        if (pointer < createdFramebuffers.length && createdFramebuffers[pointer] != pointer) {
            if (createdFramebuffers[pointer] < pointer) {
                throw new RuntimeException("Cannot bind fb " + pointer + ", it never was created");
            } else {
                throw new RuntimeException("Cannot bind fb " + pointer + ", it was destroyed");
            }
        }
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
        int address = tmpInt1[0];
        if (print || printFramebuffers) System.out.println("glGenFramebuffers() -> " + address);
        check();
        if (address >= 0 && address < createdFramebuffers.length) {
            if (createdFramebuffers[address] == address) {
                throw new RuntimeException("glGenFramebuffers() returned " + address + " twice");
            }
            createdFramebuffers[address] = address;
        }
        return address;
    }

    public static void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        check();
        GLES20.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
        if (print)
            System.out.println("glFramebufferTexture2D(" + getTextureTarget(target) + ", " + getAttachment(attachment) + ", " +
                    getTextureTarget(textureTarget) + ", " + texture + ", " + level + ")");
        check();
    }

    public static void glBlitFramebuffer(
            int x0, int y0, int w0, int h0,
            int x1, int y1, int w1, int h1,
            int bits, int flags) {
        if (Build.VERSION.SDK_INT >= 18) {
            check();
            GLES30.glBlitFramebuffer(x0, y0, w0, h0, x1, y1, w1, h1, bits, flags);
            check();
        } else
            throw new RuntimeException("Operation glTexImage2DMultisample is not supported in Android API " + Build.VERSION.SDK_INT + ", min 18 is required");

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDrawBuffer(int buffer) {
        tmpInt1[0] = buffer;
        check();
        GLES30.glDrawBuffers(1, tmpInt1, 0);
        if (print)
            System.out.println("glDrawBuffers(" + getAttachment(buffer) + ")");
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void glDrawBuffers(int[] buffers) {
        check();
        GLES30.glDrawBuffers(buffers.length, buffers, 0);
        if (print)
            System.out.println("glDrawBuffers(" + Arrays.toString(buffers) + ")");
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

    @SuppressWarnings("CommentedOutCode")
    public static int glCheckNamedFramebufferStatus(int pointer, int target) {
        check();
        // named version is not available in OpenGL ES
        if (pointer != boundFramebuffer) GLES20.glBindFramebuffer(target, pointer);
        int status = GLES20.glCheckFramebufferStatus(target);
        if (pointer != boundFramebuffer) GLES20.glBindFramebuffer(target, boundFramebuffer);
        if (print)
            System.out.println("glCheckNamedFramebufferStatus(" + pointer + ", " + getTextureTarget(target) + ") -> " + status);
        check();
        // maybe... doesn't help either...
        /*if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
            GLES20.glClear(-1);
        }*/
        return status;
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        if (framebuffer < createdFramebuffers.length && createdFramebuffers[framebuffer] != framebuffer)
            throw new RuntimeException("Cannot delete non-existing framebuffer");
        if (framebuffer < createdFramebuffers.length) createdFramebuffers[framebuffer]++;
        if (boundFramebuffer == framebuffer)
            throw new RuntimeException("Cannot delete a bound framebuffer");
        tmpInt1[0] = framebuffer;
        check();
        GLES20.glDeleteFramebuffers(1, tmpInt1, 0);
        if (print || printDeletes) System.out.println("glDeleteFramebuffers(" + framebuffer + ")");
        check();
    }

    public static int glCreateProgram() {
        int program = GLES20.glCreateProgram();
        if (print) System.out.println("glCreateProgram() -> " + program);
        return program;
    }

    public static void glDeleteProgram(int program) {
        GLES20.glDeleteProgram(program);
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
        if (print || printDeletes) System.out.println("glDeleteShader(" + shader + ")");
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

    public static void glUniform2i(int uniform, int x, int y) {
        check();
        GLES20.glUniform2i(uniform, x, y);
        if (print) System.out.println("glUniform2i(" + uniform + ", " + x + ", " + y + ")");
        check();
    }

    public static void glUniform2f(int uniform, float x, float y) {
        check();
        GLES20.glUniform2f(uniform, x, y);
        if (print) System.out.println("glUniform2f(" + uniform + ", " + x + ", " + y + ")");
        check();
    }

    public static void glUniform3i(int uniform, int x, int y, int z) {
        check();
        GLES20.glUniform3i(uniform, x, y, z);
        if (print)
            System.out.println("glUniform3i(" + uniform + ", " + x + ", " + y + ", " + z + ")");
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

    public static void glUniform4i(int uniform, int x, int y, int z, int w) {
        check();
        GLES20.glUniform4i(uniform, x, y, z, w);
        if (print)
            System.out.println("glUniform4i(" + uniform + ", " + x + ", " + y + ", " + z + ", " + w + ")");
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
        if (print || printDeletes) System.out.println("glDeleteBuffers(" + i + ")");
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
        if (print || printDeletes) System.out.println("glDeleteVertexArrays(" + i + ")");
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

    private static void checkProgramStatus() {
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

    private static void absorbErrors() {
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
        if (print)
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

    private static boolean warnedDTT = false;

    public static void glDeleteTextures(int[] textures) {
        check();
        for (int texToDelete : textures) {
            if (texToDelete < createdTextures.length) {
                if (createdTextures[texToDelete] == texToDelete) {
                    createdTextures[texToDelete]++;
                } else if (!warnedDTT) {
                    warnedDTT = true;
                    new RuntimeException("Cannot delete texture " + texToDelete + " twice")
                            .printStackTrace();
                }
            }
            for (int boundTexture : boundTextures) {
                if (texToDelete == boundTexture) {
                    new RuntimeException("Cannot delete bound texture " + texToDelete)
                            .printStackTrace();
                }
            }
        }
        GLES20.glDeleteTextures(textures.length, textures, 0);
        if (print || printDeletes)
            System.out.println("glDeleteTexture(" + Arrays.toString(textures) + ")");
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
        if (print) System.out.println("glReadPixels(" + x + ", " + y + ", " + w + ", " + h +
                ", " + getFormat(format) + ", " + getType(type) + ", " + pixels + ")");
        GLES20.glReadPixels(x, y, w, h, format, type, pixels);
        check();
    }

    public static void glScissor(int x, int y, int w, int h) {
        check();
        if (print) System.out.println("glScissor(" + x + ", " + y + ", " + w + ", " + h + ")");
        GLES11.glScissor(x, y, w, h);
        check();
    }

    public static String glGetString(int name) {
        return GLES11.glGetString(name);
    }

    public static void glReadPixels(int x, int y, int w, int h, int format, int type, int[] buffer) {
        // could be optimized to use a static temporary buffer
        ByteBuffer tmp = ByteBuffer.allocateDirect(buffer.length * 4);
        tmp.order(ByteOrder.nativeOrder());
        GLES11.glReadPixels(x, y, w, h, format, type, tmp);
        tmp.position(0);
        tmp.asIntBuffer().get(buffer);
        MemoryUtil.memFree(tmp);
    }

    public static void glDebugMessageCallback(GLDebugMessageCallbackI callbackI, long userParam) {
        if (Build.VERSION.SDK_INT >= 24) {
            check();
            // not yet implemented???
            try {
                GLES32.glDebugMessageCallback(callbackI);
            } catch (UnsupportedOperationException e) {
                System.out.println("[WARN] glDebugMessageCallback is not supported");
            } catch (Exception e) {
                e.printStackTrace();
            }
            check();
        }
    }

}
