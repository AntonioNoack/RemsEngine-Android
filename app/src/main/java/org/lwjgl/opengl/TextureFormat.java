package org.lwjgl.opengl;

import static android.opengl.GLES10.GL_ALPHA;
import static android.opengl.GLES10.GL_BYTE;
import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_LUMINANCE;
import static android.opengl.GLES10.GL_LUMINANCE_ALPHA;
import static android.opengl.GLES10.GL_RGB;
import static android.opengl.GLES10.GL_RGBA;
import static android.opengl.GLES10.GL_SHORT;
import static android.opengl.GLES10.GL_UNSIGNED_BYTE;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT_4_4_4_4;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT_5_5_5_1;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT_5_6_5;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES30.GL_DEPTH24_STENCIL8;
import static android.opengl.GLES30.GL_DEPTH32F_STENCIL8;
import static android.opengl.GLES30.GL_DEPTH_COMPONENT24;
import static android.opengl.GLES30.GL_DEPTH_COMPONENT32F;
import static android.opengl.GLES30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
import static android.opengl.GLES30.GL_HALF_FLOAT;
import static android.opengl.GLES30.GL_INT;
import static android.opengl.GLES30.GL_R11F_G11F_B10F;
import static android.opengl.GLES30.GL_R16F;
import static android.opengl.GLES30.GL_R16I;
import static android.opengl.GLES30.GL_R16UI;
import static android.opengl.GLES30.GL_R32F;
import static android.opengl.GLES30.GL_R32I;
import static android.opengl.GLES30.GL_R32UI;
import static android.opengl.GLES30.GL_R8;
import static android.opengl.GLES30.GL_R8I;
import static android.opengl.GLES30.GL_R8UI;
import static android.opengl.GLES30.GL_R8_SNORM;
import static android.opengl.GLES30.GL_RED;
import static android.opengl.GLES30.GL_RED_INTEGER;
import static android.opengl.GLES30.GL_RG;
import static android.opengl.GLES30.GL_RG16F;
import static android.opengl.GLES30.GL_RG16I;
import static android.opengl.GLES30.GL_RG16UI;
import static android.opengl.GLES30.GL_RG32F;
import static android.opengl.GLES30.GL_RG32I;
import static android.opengl.GLES30.GL_RG32UI;
import static android.opengl.GLES30.GL_RG8;
import static android.opengl.GLES30.GL_RG8I;
import static android.opengl.GLES30.GL_RG8UI;
import static android.opengl.GLES30.GL_RG8_SNORM;
import static android.opengl.GLES30.GL_RGB10_A2;
import static android.opengl.GLES30.GL_RGB10_A2UI;
import static android.opengl.GLES30.GL_RGB16F;
import static android.opengl.GLES30.GL_RGB16I;
import static android.opengl.GLES30.GL_RGB16UI;
import static android.opengl.GLES30.GL_RGB32F;
import static android.opengl.GLES30.GL_RGB32I;
import static android.opengl.GLES30.GL_RGB32UI;
import static android.opengl.GLES30.GL_RGB565;
import static android.opengl.GLES30.GL_RGB5_A1;
import static android.opengl.GLES30.GL_RGB8;
import static android.opengl.GLES30.GL_RGB8I;
import static android.opengl.GLES30.GL_RGB8UI;
import static android.opengl.GLES30.GL_RGB8_SNORM;
import static android.opengl.GLES30.GL_RGB9_E5;
import static android.opengl.GLES30.GL_RGBA16F;
import static android.opengl.GLES30.GL_RGBA16I;
import static android.opengl.GLES30.GL_RGBA16UI;
import static android.opengl.GLES30.GL_RGBA32F;
import static android.opengl.GLES30.GL_RGBA32I;
import static android.opengl.GLES30.GL_RGBA32UI;
import static android.opengl.GLES30.GL_RGBA4;
import static android.opengl.GLES30.GL_RGBA8;
import static android.opengl.GLES30.GL_RGBA8I;
import static android.opengl.GLES30.GL_RGBA8UI;
import static android.opengl.GLES30.GL_RGBA8_SNORM;
import static android.opengl.GLES30.GL_RGBA_INTEGER;
import static android.opengl.GLES30.GL_RGB_INTEGER;
import static android.opengl.GLES30.GL_RG_INTEGER;
import static android.opengl.GLES30.GL_SRGB8;
import static android.opengl.GLES30.GL_SRGB8_ALPHA8;
import static android.opengl.GLES30.GL_UNSIGNED_INT;
import static android.opengl.GLES30.GL_UNSIGNED_INT_10F_11F_11F_REV;
import static android.opengl.GLES30.GL_UNSIGNED_INT_24_8;
import static android.opengl.GLES30.GL_UNSIGNED_INT_2_10_10_10_REV;
import static android.opengl.GLES30.GL_UNSIGNED_INT_5_9_9_9_REV;

public class TextureFormat {

    final int internalFormat, format, type;

    public TextureFormat(int internalFormat, int format, int type) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }

    public static boolean isSupported(int internalFormat, int format, int type) {
        for (TextureFormat tex : supportedFormats) {
            if (tex.internalFormat == internalFormat && tex.format == format && tex.type == type)
                return true;
        }
        return false;
    }

    public static TextureFormat[] supportedFormats = new TextureFormat[]{
            // unsized formats:
            new TextureFormat(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGB, GL_RGB, GL_UNSIGNED_SHORT_5_6_5),
            new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4),
            new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_SHORT_5_5_5_1),
            new TextureFormat(GL_LUMINANCE_ALPHA, GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_LUMINANCE, GL_LUMINANCE, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_ALPHA, GL_ALPHA, GL_UNSIGNED_BYTE),
            // sizes formats:
            new TextureFormat(GL_R8, GL_RED, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_R8_SNORM, GL_RED, GL_BYTE),
            new TextureFormat(GL_R16F, GL_RED, GL_HALF_FLOAT),
            new TextureFormat(GL_R16F, GL_RED, GL_FLOAT),
            new TextureFormat(GL_R32F, GL_RED, GL_FLOAT),
            new TextureFormat(GL_R8UI, GL_RED_INTEGER, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_R8I, GL_RED_INTEGER, GL_BYTE),
            new TextureFormat(GL_R16UI, GL_RED_INTEGER, GL_UNSIGNED_SHORT),
            new TextureFormat(GL_R16I, GL_RED_INTEGER, GL_SHORT),
            new TextureFormat(GL_R32UI, GL_RED_INTEGER, GL_UNSIGNED_INT),
            new TextureFormat(GL_R32I, GL_RED_INTEGER, GL_INT),
            new TextureFormat(GL_RG8, GL_RG, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RG8_SNORM, GL_RG, GL_BYTE),
            new TextureFormat(GL_RG16F, GL_RG, GL_HALF_FLOAT),
            new TextureFormat(GL_RG16F, GL_RG, GL_FLOAT),
            new TextureFormat(GL_RG32F, GL_RG, GL_FLOAT),
            new TextureFormat(GL_RG8UI, GL_RG_INTEGER, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RG8I, GL_RG_INTEGER, GL_BYTE),
            new TextureFormat(GL_RG16UI, GL_RG_INTEGER, GL_UNSIGNED_SHORT),
            new TextureFormat(GL_RG16I, GL_RG_INTEGER, GL_SHORT),
            new TextureFormat(GL_RG32UI, GL_RG_INTEGER, GL_UNSIGNED_INT),
            new TextureFormat(GL_RG32I, GL_RG_INTEGER, GL_INT),
            new TextureFormat(GL_RGB8, GL_RGB, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_SRGB8, GL_RGB, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGB565, GL_RGB, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGB565, GL_RGB, GL_UNSIGNED_SHORT_5_6_5),
            new TextureFormat(GL_RGB8_SNORM, GL_RGB, GL_BYTE),
            new TextureFormat(GL_R11F_G11F_B10F, GL_RGB, GL_UNSIGNED_INT_10F_11F_11F_REV),
            new TextureFormat(GL_R11F_G11F_B10F, GL_RGB, GL_HALF_FLOAT),
            new TextureFormat(GL_R11F_G11F_B10F, GL_RGB, GL_FLOAT),
            new TextureFormat(GL_RGB9_E5, GL_RGB, GL_UNSIGNED_INT_5_9_9_9_REV),
            new TextureFormat(GL_RGB9_E5, GL_RGB, GL_HALF_FLOAT),
            new TextureFormat(GL_RGB9_E5, GL_RGB, GL_FLOAT),
            new TextureFormat(GL_RGB16F, GL_RGB, GL_HALF_FLOAT),
            new TextureFormat(GL_RGB16F, GL_RGB, GL_FLOAT),
            new TextureFormat(GL_RGB32F, GL_RGB, GL_FLOAT),
            new TextureFormat(GL_RGB8UI, GL_RGB_INTEGER, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGB8I, GL_RGB_INTEGER, GL_BYTE),
            new TextureFormat(GL_RGB16UI, GL_RGB_INTEGER, GL_UNSIGNED_SHORT),
            new TextureFormat(GL_RGB16I, GL_RGB_INTEGER, GL_SHORT),
            new TextureFormat(GL_RGB32UI, GL_RGB_INTEGER, GL_UNSIGNED_INT),
            new TextureFormat(GL_RGB32I, GL_RGB_INTEGER, GL_INT),
            new TextureFormat(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_SRGB8_ALPHA8, GL_RGBA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGBA8_SNORM, GL_RGBA, GL_BYTE),
            new TextureFormat(GL_RGB5_A1, GL_RGBA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGB5_A1, GL_RGBA, GL_UNSIGNED_SHORT_5_5_5_1),
            new TextureFormat(GL_RGB5_A1, GL_RGBA, GL_UNSIGNED_INT_2_10_10_10_REV),
            new TextureFormat(GL_RGBA4, GL_RGBA, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGBA4, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4),
            new TextureFormat(GL_RGB10_A2, GL_RGBA, GL_UNSIGNED_INT_2_10_10_10_REV),
            new TextureFormat(GL_RGBA16F, GL_RGBA, GL_HALF_FLOAT),
            new TextureFormat(GL_RGBA16F, GL_RGBA, GL_FLOAT),
            new TextureFormat(GL_RGBA32F, GL_RGBA, GL_FLOAT),
            new TextureFormat(GL_RGBA8UI, GL_RGBA_INTEGER, GL_UNSIGNED_BYTE),
            new TextureFormat(GL_RGBA8I, GL_RGBA_INTEGER, GL_BYTE),
            new TextureFormat(GL_RGB10_A2UI, GL_RGBA_INTEGER, GL_UNSIGNED_INT_2_10_10_10_REV),
            new TextureFormat(GL_RGBA16UI, GL_RGBA_INTEGER, GL_UNSIGNED_SHORT),
            new TextureFormat(GL_RGBA16I, GL_RGBA_INTEGER, GL_SHORT),
            new TextureFormat(GL_RGBA32I, GL_RGBA_INTEGER, GL_INT),
            new TextureFormat(GL_RGBA32UI, GL_RGBA_INTEGER, GL_UNSIGNED_INT),
            // depth formats:
            new TextureFormat(GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT),
            new TextureFormat(GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT),
            new TextureFormat(GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT),
            new TextureFormat(GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT, GL_FLOAT),
            new TextureFormat(GL_DEPTH24_STENCIL8, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT_24_8),
            new TextureFormat(GL_DEPTH32F_STENCIL8, GL_DEPTH_COMPONENT, GL_FLOAT_32_UNSIGNED_INT_24_8_REV),
    };

}
