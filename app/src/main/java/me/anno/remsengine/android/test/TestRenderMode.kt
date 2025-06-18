package me.anno.remsengine.android.test

import me.anno.engine.ui.render.RenderMode
import me.anno.engine.ui.render.RenderMode.Companion.opaqueNodeSettings
import me.anno.graph.visual.render.QuickPipeline
import me.anno.graph.visual.render.effects.SSAONode
import me.anno.graph.visual.render.scene.RenderDeferredNode

object TestRenderMode {
    val testRenderMode = lazy {
        RenderMode(
            "Testing",
            QuickPipeline()
                .then1(RenderDeferredNode(), opaqueNodeSettings)
                .then(
                    SSAONode(),
                    mapOf("Strength" to 1000f),
                    mapOf("Ambient Occlusion" to listOf("Illuminated"))
                )
                .finish()
        )
    }
}