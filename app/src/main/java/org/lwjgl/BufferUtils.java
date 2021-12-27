package org.lwjgl;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class BufferUtils {

    public static FloatBuffer createFloatBuffer(int size){
        ByteBuffer bytes = MemoryUtil.memAlloc(size * 4);
        return bytes.asFloatBuffer();
    }

}
