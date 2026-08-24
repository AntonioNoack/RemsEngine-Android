package me.anno.remsengine.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import me.anno.fonts.Font
import me.anno.fonts.FontImpl
import me.anno.image.raw.IntImage
import me.anno.remsengine.android.AndroidPlugin.getPaint
import me.anno.utils.Color.a
import me.anno.utils.types.Floats.toIntOr
import me.anno.utils.types.Strings.joinChars

class TextGeneratorImpl() :
    FontImpl<List<TextGeneratorImpl>>() {

    // top, bottom: -29.507143, 7.571181
    private fun getTextHeight(metrics: Paint.FontMetrics) = (metrics.bottom - metrics.top).toIntOr()

    override fun getFallbackFonts(font: Font): List<TextGeneratorImpl> = emptyList()

    override fun getTextLength(font: Font, codepoint: Int): Int {
        return getPaint(font).measureText(codepoint.joinChars()).toInt()
    }

    override fun getTextLength(font: Font, codepointA: Int, codepointB: Int): Int {
        return getPaint(font).measureText(codepointA.joinChars() + codepointB.joinChars()).toInt()
    }

    override fun drawGlyph(
        image: IntImage,
        x0: Int, x1: Int,
        y0: Int, y1: Int,
        strictBounds: Boolean, font: Font,
        fallbackFonts: List<TextGeneratorImpl>, fontIndex: Int, codepoint: Int,
        textColor: Int, backgroundColor: Int, portableImages: Boolean
    ) {
        val paint = getPaint(font)
        val width = x1 - x0
        val height = y1 - y0

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        // fill background with that color
        paint.color = backgroundColor
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        paint.isSubpixelText = !portableImages
        val y = paint.textSize
        val x = width * 0.5f

        canvas.drawText(codepoint.joinChars(), x, y, paint)

        val pixels = getPixels(bitmap)
        for (y in y0 until y1) {
            for (x in x0 until x1) {
                val color = pixels[(x - x0) + (y - y0) * width]
                if (color.a() == 0) continue
                image.setRGB(x, y, color)
            }
        }
    }

    override fun getBaselineY(font: Font): Float = -getPaint(font).fontMetrics.top
    override fun getLineHeight(font: Font): Float = getTextHeight(getPaint(font).fontMetrics).toFloat()

    override fun getSupportLevel(
        fonts: List<TextGeneratorImpl>,
        codepoint: Int, lastSupportLevel: Int
    ): Int = 0

    private fun getPixels(bitmap: Bitmap): IntArray {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        bitmap.recycle()
        return pixels
    }

}
