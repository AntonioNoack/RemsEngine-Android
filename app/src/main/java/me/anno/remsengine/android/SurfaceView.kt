package me.anno.remsengine.android

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import me.anno.input.Input
import me.anno.input.Key
import me.anno.input.Touch
import me.anno.studio.Events.addEvent

@SuppressLint("ViewConstructor")
class SurfaceView(private val ctx: MainActivity) : GLSurfaceView(ctx) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pid = event.getPointerId(event.actionIndex)
        val isMouse = pid == 0
        val x = event.x
        val y = event.y
        if (MainActivity.lastMouseX != x || MainActivity.lastMouseY != y) {
            MainActivity.lastMouseX = x
            MainActivity.lastMouseY = y
            addEvent {
                Touch.onTouchMove(pid, x, y)
                if (isMouse) Input.onMouseMove(ctx.osWindow, x, y)
            }
            // only if there is a single pointer?
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> addEvent {
                Touch.onTouchDown(pid, x, y)
                if (isMouse) Input.onMousePress(ctx.osWindow, Key.BUTTON_LEFT)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> addEvent {
                Touch.onTouchUp(pid, x, y)
                if (isMouse) Input.onMouseRelease(ctx.osWindow, Key.BUTTON_LEFT)
                // update mouse position, when the gesture is finished (no more touches down)?
            }
        }
        ctx.detector.onTouchEvent(event)
        return true
    }
}