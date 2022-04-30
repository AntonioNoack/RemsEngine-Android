package org.lwjgl.opengl;


import static android.opengl.GLES10.GL_BYTE;
import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_FALSE;
import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES10.GL_LINEAR_MIPMAP_NEAREST;
import static android.opengl.GLES10.GL_LINES;
import static android.opengl.GLES10.GL_LINE_STRIP;
import static android.opengl.GLES10.GL_LUMINANCE;
import static android.opengl.GLES10.GL_LUMINANCE_ALPHA;
import static android.opengl.GLES10.GL_NEAREST;
import static android.opengl.GLES10.GL_NEAREST_MIPMAP_LINEAR;
import static android.opengl.GLES10.GL_NEAREST_MIPMAP_NEAREST;
import static android.opengl.GLES10.GL_REPEAT;
import static android.opengl.GLES10.GL_RGB;
import static android.opengl.GLES10.GL_RGBA;
import static android.opengl.GLES10.GL_SHORT;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.GL_TRIANGLES;
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

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_SWIZZLE_RGBA;
import static javax.microedition.khronos.opengles.GL11ExtensionPack.GL_DEPTH_COMPONENT32;
import static javax.microedition.khronos.opengles.GL11ExtensionPack.GL_MIRRORED_REPEAT;

public class GLStrings {

    public static String getDrawMode(int mode) {
        switch (mode) {
            case GL_TRIANGLES:
                return "TRIANGLES";
            case GL_TRIANGLE_FAN:
                return "TRIANGLE_FAN";
            case GL_TRIANGLE_STRIP:
                return "TRIANGLE_STRIP";
            case GL_QUADS:
                throw new IllegalArgumentException("QUADS are not supported on Android!");
            case GL_LINES:
                return "LINES";
            case GL_LINE_STRIP:
                return "LINE_STRIP";
            default:
                return mode + "";
        }
    }

    public static String getTextureTarget(int target) {
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

    public static String getAttachment(int attachment) {
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

    public static String getBufferTarget(int target) {
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

    public static String getBufferUsage(int target) {
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

    public static String getType(int type) {
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

    public static String getFormat(int format) {
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

    public static String getTexKey(int key) {
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
            case GL_TEXTURE_SWIZZLE_RGBA:
                return "TEXTURE_SWIZZLE_RGBA";
            default:
                return key + "";
        }
    }

    public static String getTexValue(int value) {
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

}
