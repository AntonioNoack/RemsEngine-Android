package me.anno.remsengine.android

import android.view.GestureDetector
import android.view.MotionEvent
import me.anno.engine.Events.addEvent
import me.anno.gpu.GFX
import me.anno.input.Input
import me.anno.input.Key
import org.apache.logging.log4j.LogManager

class GestureHandler : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    companion object {
        private val LOGGER = LogManager.getLogger(GestureHandler::class)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // todo how can we use that, would be ever use it?
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        addEvent {
            val osWindow = GFX.someWindow
            Input.onMousePress(osWindow, Key.BUTTON_RIGHT)
            Input.onMouseRelease(osWindow, Key.BUTTON_RIGHT)
            LOGGER.info("Long Press")
        }
    }

    override fun onShowPress(e: MotionEvent) {
        // mmh...
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        // double click, but we have implemented that ourselves anyways
        // addEvent { DebugGPUStorage.openMenu() }
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // addEvent { Input.onMouseWheel(distanceX, distanceY, false) }
        return false
    }

}