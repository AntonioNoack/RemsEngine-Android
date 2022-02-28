package org.lwjgl.glfw;

import me.anno.remsengine.MainActivity;

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

    public static void glfwGetCursorPos(long window, double[] x, double[] y) {
        x[0] = MainActivity.Companion.getLastMouseX();
        y[0] = MainActivity.Companion.getLastMouseY();
    }

}
