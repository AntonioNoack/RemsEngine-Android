package me.anno.remsengine.android.test

import me.anno.ecs.Component
import me.anno.ecs.annotations.DebugAction
import me.anno.engine.ui.control.DraggingControls
import me.anno.engine.ui.render.RenderView

class TestControls : Component() {
    @DebugAction
    fun resetView() {
        val instance = RenderView.currentInstance!!
        val controls = instance.controlScheme as DraggingControls
        controls.resetCamera()
        instance.radius = 3f
    }
}