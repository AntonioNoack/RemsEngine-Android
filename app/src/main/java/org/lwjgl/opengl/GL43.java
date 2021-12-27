package org.lwjgl.opengl;

import android.opengl.GLES32;
import android.os.Build;

public class GL43 extends GL33 {

    public static void glDebugMessageCallback(GLDebugMessageCallbackI callbackI, long userParam) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            check();
            // not yet implemented???
            try {
                GLES32.glDebugMessageCallback(callbackI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            check();
        }
    }

}
