package org.lwjgl;

import org.lwjgl.system.CustomBuffer;

@SuppressWarnings("unused")
public class PointerBuffer extends CustomBuffer {

    private final long[] pointers;

    @Override
    public int capacity() {
        return pointers.length;
    }

    public PointerBuffer(int size) {
        pointers = new long[size];
    }

    public static PointerBuffer allocateDirect(int size) {
        return new PointerBuffer(size);
    }

}
