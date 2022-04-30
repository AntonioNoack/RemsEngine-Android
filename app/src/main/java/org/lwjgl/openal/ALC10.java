package org.lwjgl.openal;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class ALC10 {

    private static final long devicePointer = 1 << 16;

    public static long alcOpenDevice(ByteBuffer nameOrNull) {
        return devicePointer;
    }

}
