package org.lwjgl.system;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MemoryUtil {

    public static ByteBuffer memAlloc(int size) {
        return ByteBuffer.allocateDirect(size)
                .order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer memAllocFloat(int size) {
        return memAlloc(size * 4).asFloatBuffer();
    }

    public static ShortBuffer memAllocShort(int size) {
        return memAlloc(size * 2).asShortBuffer();
    }

    public static void memFree(Buffer buffer) {
        // todo delete this buffer
    }

}
