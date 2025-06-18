package me.anno.remsengine.android

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import me.anno.engine.Events.addEvent
import me.anno.gpu.GFX
import me.anno.input.Input
import me.anno.input.Key
import me.anno.input.Touch
import me.anno.ui.input.InputPanel

/**
 * Where the graphics are drawn onto; catches all events for later handling.
 * */
@SuppressLint("ViewConstructor")
class SurfaceView(private val ctx: MainActivity) : GLSurfaceView(ctx) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index = event.actionIndex
        val pid = event.getPointerId(index)
        val isMouse = pid == 0 && event.pointerCount == 1
        val x = event.x
        val y = event.y
        if (isMouse && (MainActivity.lastMouseX != x || MainActivity.lastMouseY != y)) {
            MainActivity.lastMouseX = x
            MainActivity.lastMouseY = y
            addEvent {
                Input.onMouseMove(ctx.osWindow, x, y)
            }
            // only if there is a single pointer?
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                addEvent {
                    Touch.onTouchDown(pid, x, y)
                    if (isMouse) Input.onMousePress(ctx.osWindow, Key.BUTTON_LEFT)
                    val window = GFX.someWindow.windowStack
                    val inFocus0 = window.inFocus0
                    val inFocusInput = inFocus0?.listOfHierarchy
                        ?.firstOrNull { it is InputPanel<*> && it.value != Unit }
                    if (inFocusInput is InputPanel<*>) {
                        ctx.requestKeyboard(inFocus0, inFocusInput)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val xs = FloatArray(event.pointerCount)
                val ys = FloatArray(event.pointerCount)
                val ids = IntArray(event.pointerCount)
                for (i in 0 until event.pointerCount) {
                    ids[i] = event.getPointerId(i)
                    xs[i] = event.getX(i)
                    ys[i] = event.getY(i)
                }
                addEvent {
                    for (i in xs.indices) {
                        Touch.onTouchMove(ids[i], xs[i], ys[i])
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                addEvent {
                    Touch.onTouchUp(pid, x, y)
                    if (isMouse) Input.onMouseRelease(ctx.osWindow, Key.BUTTON_LEFT)
                    // update mouse position, when the gesture is finished (no more touches down)?
                }
            }
        }
        ctx.detector.onTouchEvent(event)
        return true
    }
}