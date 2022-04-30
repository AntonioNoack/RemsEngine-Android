package org.lwjgl.openal;

import static org.lwjgl.openal.EXTDisconnect.ALC_CONNECTED;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@SuppressWarnings("unused")
public class AL {

    private static final long devicePointer = 1 << 16;
    private static final ALCCapabilities capabilities = new ALCCapabilities();

    public static ALCCapabilities createCapabilities(long device) {
        return capabilities;
    }

    public static ALCapabilities createCapabilities(ALCCapabilities capabilities) {
        return capabilities;
    }

    public static long alcOpenDevice(ByteBuffer nameOrNull) {
        return devicePointer;
    }

    public static long alcCreateContext(long device, IntBuffer attributesOrNull) {
        return device; // ^^
    }

    public static boolean alcMakeContextCurrent(long context) {
        // returns true on success
        return true;
    }

    public static void alcDestroyContext(long context) {
    }

    public static void alcCloseDevice(long device) {
    }

    public static int alGetError() {
        return 0;
    }

    // device, ALC_CONNECTED, answer
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static void alcGetIntegerv(long device, int type, int[] answer) {
        switch (type) {
            case ALC_CONNECTED:
                answer[0] = 1;
                break;
            default:
                throw new RuntimeException("Unknown OpenAL type " + type);
        }
    }

}
