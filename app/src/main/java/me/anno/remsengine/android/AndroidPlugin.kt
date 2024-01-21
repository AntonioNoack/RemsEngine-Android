package me.anno.remsengine.android

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import me.anno.extensions.plugins.Plugin
import me.anno.fonts.Font
import me.anno.fonts.FontStats
import me.anno.fonts.signeddistfields.Contour
import me.anno.fonts.signeddistfields.edges.LinearSegment
import me.anno.image.Image
import me.anno.image.ImageCache
import me.anno.image.raw.IntImage
import me.anno.io.MediaMetadata
import me.anno.maths.geometry.MarchingSquares
import me.anno.utils.types.Booleans.toInt
import org.joml.AABBf
import java.io.File
import java.io.OutputStream

object AndroidPlugin : Plugin() {

    override fun onEnable() {
        super.onEnable()

        MediaMetadata.registerSignatureHandler(100, "ImageIO") { file, signature, dst ->
            when (signature) {
                "png", "jpg", "webp" -> {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    val bitmap = BitmapFactory.decodeStream(file.inputStreamSync(), null, options)
                    dst.setImage(options.outWidth, options.outHeight)
                    bitmap?.recycle() // should be null
                    true
                }
                else -> false
            }
        }

        for (signature in listOf("png", "jpg", "gif", "bmp", "webp")) {
            ImageCache.registerStreamReader(signature) { it, callback ->
                val bitmap = BitmapFactory.decodeStream(it)
                val values = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(values, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val image = IntImage(bitmap.width, bitmap.height, values, bitmap.hasAlpha())
                bitmap.recycle()
                callback.ok(image)
            }
        }

        Image.writeImageImpl = this::writeImage

        FontStats.queryInstalledFontsImpl = ::getFonts
        FontStats.getTextGeneratorImpl = { key -> TextGen(key) }
        FontStats.getTextLengthImpl = { font, text ->
            val paint = getPaint(font)
            val rect = Rect()
            paint.getTextBounds(text, 0, text.length, rect)
            rect.right.toDouble()
        }
        FontStats.getFontHeightImpl = { font ->
            val metrics = getPaint(font).fontMetrics
            // top, bottom: -29.507143, 7.571181
            (metrics.bottom - metrics.top).toDouble()
        }
        Contour.calculateContoursImpl = { font, text ->
            val pixels = TextToPixels.createPixels(getPaint(font), text.toString())
            if (pixels != null) MarchingSquares.march(
                pixels.w, pixels.h, pixels.values, 127.5f,
                AABBf(0f, 0f, 0f, (pixels.w - 1).toFloat(), (pixels.h - 1).toFloat(), 0f)
            ).map { pts ->
                Contour(pts.indices.map {
                    val p0 = pts[it]
                    val p1 = if (it == pts.size - 1) pts[0] else pts[it + 1]
                    LinearSegment(p0, p1)
                })
            } else emptyList()
        }
    }

    fun getPaint(font: Font): Paint {
        return getPaint(font.name, font.size, font.isBold, font.isItalic)
    }

    private fun getPaint(name: String, size: Float, isBold: Boolean, isItalic: Boolean): Paint {
        val paint = Paint()
        val style = isBold.toInt(Typeface.BOLD) +
                isItalic.toInt(Typeface.ITALIC)
        paint.typeface = Typeface.create(name, style)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = size
        paint.isAntiAlias = true
        return paint
    }

    private fun getFonts(): List<String> {
        val fontFiles = File("/system/fonts").listFiles()
        return fontFiles?.filter { !it.isDirectory }?.map { it.nameWithoutExtension }
            ?: listOf("Droid Sans", "Droid Serif", "Droid Sans Mono", "Roboto")
    }

    private fun writeImage(img: Image, dst: OutputStream, format: String, quality: Float) {
        val intImage = img.createIntImage()
        val intData = intImage.data
        val bitmap = Bitmap.createBitmap(intData, img.width, img.height, Bitmap.Config.ARGB_8888)
        var format1 = CompressFormat.PNG
        if ("webp".equals(format, true)) {
            format1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (quality == 1f) CompressFormat.WEBP_LOSSLESS
                else CompressFormat.WEBP_LOSSY
            } else CompressFormat.WEBP
        } else if ("jpg".equals(format, true) || "jpeg".equals(format, true)) {
            format1 = CompressFormat.JPEG
        } else if (!"png".equals(format, true)) {
            println("$format cannot be properly exported, using PNG")
        }
        bitmap.compress(format1, (quality * 100).toInt(), dst)
        bitmap.recycle()
    }
}