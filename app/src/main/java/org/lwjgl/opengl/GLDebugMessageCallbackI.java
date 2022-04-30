package org.lwjgl.opengl;

import android.opengl.GLES32;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = 24)
public interface GLDebugMessageCallbackI extends GLES32.DebugProc {
}
