package org.lwjgl.glfw;

public class GLFW {

    public static void glfwTerminate() {
        // why...
    }

    public static long glfwCreateStandardCursor(int mode) {
        return (long) mode;
    }

    public static boolean glfwJoystickPresent(int index) {
        return false;
    }

    public static void glfwSetCursor(long window, long cursor) {
        // todo change cursor???
    }

}
