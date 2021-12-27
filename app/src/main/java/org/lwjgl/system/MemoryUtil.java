package org.lwjgl.system;

import java.lang.reflect.Method;
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

    public static void memFree(Buffer toBeDestroyed) {
        if (toBeDestroyed.isDirect()) {
            try {
                Method cleanerMethod = toBeDestroyed.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(toBeDestroyed);
                if (cleaner != null) {
                    Method cleanMethod = cleaner.getClass().getMethod("clean");
                    cleanMethod.setAccessible(true);
                    cleanMethod.invoke(cleaner);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
