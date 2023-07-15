package org.lwjgl.glfw;

import me.anno.remsengine.android.MainActivity;

@SuppressWarnings("unused")
public class GLFW {

    public static void glfwTerminate() {
    }

    public static long glfwCreateStandardCursor(int mode) {
        return mode;
    }

    @SuppressWarnings("SameReturnValue")
    public static boolean glfwJoystickPresent(int index) {
        // to do check for controller whether it's available
        // our controller or method to connect the two is not compatible ... -> do this later
        return false;
    }

    // change cursor? can we do that for Android mice?
    public static void glfwSetCursor(long window, long cursor) {
    }

    public static void glfwGetCursorPos(long window, double[] x, double[] y) {
        x[0] = MainActivity.Companion.getLastMouseX();
        y[0] = MainActivity.Companion.getLastMouseY();
    }

    public static boolean glfwWindowShouldClose(long window) {
        return false;
    }

    public static void glfwSetWindowTitle(long window, CharSequence title) {
        // set title?
    }

    public static void glfwWaitEventsTimeout(double timeout) {
        // nothing to do
    }

}
