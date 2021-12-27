package org.lwjgl.opengl;

import android.opengl.GLES32;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public interface GLDebugMessageCallbackI extends GLES32.DebugProc {
}
