package me.anno.audio2d

import org.joml.Quaternionf
import org.joml.Vector3f

object Listener {

    val position = Vector3f()
    val rotation = Quaternionf()

    var earOffset = 0.10f

    val leftPosition = Vector3f()
    val rightPosition = Vector3f()
    val forward = Vector3f()

    val leftDirection = Vector3f()
    val rightDirection = Vector3f()
}