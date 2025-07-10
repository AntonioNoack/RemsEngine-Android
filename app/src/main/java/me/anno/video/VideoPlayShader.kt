package me.anno.video

import me.anno.engine.ui.render.ECSMeshShader.Companion.discardByCullingPlane
import me.anno.engine.ui.render.ECSMeshShader.Companion.finalMotionCalculation
import me.anno.engine.ui.render.ECSMeshShader.Companion.normalCalculation
import me.anno.engine.ui.render.ECSMeshShaderLight
import me.anno.gpu.shader.builder.ShaderStage
import me.anno.utils.types.Booleans.hasFlag

object VideoPlayShader : ECSMeshShaderLight("VideoPlayShader") {
    override fun createFragmentStages(key: ShaderKey): List<ShaderStage> {
        return key.vertexData.onFragmentShader + listOf(
            ShaderStage(
                "material", createFragmentVariables(key), "" +
                        concatDefines(key).toString() +
                        discardByCullingPlane +
                        // step by step define all material properties
                        "   vec4 color = texture(colorTexture,uv);\n" +
                        "   finalColor = color.rgb;\n" +
                        "   finalAlpha = 1.0;\n" +
                        (if (key.flags.hasFlag(NEEDS_COLORS)) {
                            normalCalculation
                        } else "") +
                        finalMotionCalculation
            ).add(
                "" +
                        "#extension GL_OES_EGL_image_external : require\n" +
                        "#extension GL_OES_EGL_image_external_essl3 : require\n" +
                        "uniform samplerExternalOES colorTexture;\n"
            )
        )
    }
}