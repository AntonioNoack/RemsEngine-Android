package me.anno.remsengine.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class TextToPixels(val w: Int, val h: Int, val values: FloatArray) {
    companion object {
        fun createPixels(paint: Paint, text: String): TextToPixels? {
            val rect = Rect()
            paint.getTextBounds(text, 0, text.length, rect)
            var w = rect.width()
            var h = rect.height()
            if (w <= 0 || h <= 0) return null

            // padding
            w += 2
            h += 2

            // create pixels
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
            val canvas = Canvas(bitmap)
            paint.color = -1
            val dx = -rect.left + 1
            val dy = -rect.top + 1
            canvas.drawText(text, dx.toFloat(), dy.toFloat(), paint)

            val pixelInts = IntArray(w * h)
            bitmap.getPixels(pixelInts, 0, w, 0, 0, w, h)
            bitmap.recycle()

            // clearly superior compared to the old method
            // todo we could simplify the shapes, and identify quadratic and cubic splines...
            val pixels = FloatArray(w * h)
            for (i in pixelInts.indices) {
                pixels[i] = (pixelInts[i] ushr 24).toFloat()
            }
            return TextToPixels(w, h, pixels)
        }
    }
}