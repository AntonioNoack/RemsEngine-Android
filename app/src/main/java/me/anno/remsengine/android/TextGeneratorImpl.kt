package me.anno.remsengine.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import me.anno.fonts.Font
import me.anno.fonts.LineSplitter
import me.anno.fonts.TextGenerator
import me.anno.fonts.TextGroup
import me.anno.fonts.keys.FontKey
import me.anno.fonts.mesh.CharacterOffsetCache
import me.anno.gpu.GFX
import me.anno.gpu.GPUTasks
import me.anno.gpu.drawing.DrawTexts
import me.anno.gpu.drawing.GFXx2D
import me.anno.gpu.texture.FakeWhiteTexture
import me.anno.gpu.texture.ITexture2D
import me.anno.gpu.texture.Texture2D
import me.anno.gpu.texture.Texture2DArray
import me.anno.maths.Maths
import me.anno.remsengine.android.AndroidPlugin.getPaint
import me.anno.utils.Color.convertARGB2ABGR
import me.anno.utils.Color.convertARGB2RGBA
import me.anno.utils.async.Callback
import me.anno.utils.types.Floats.toIntOr
import me.anno.utils.types.Strings.isBlank2
import me.anno.utils.types.Strings.shorten
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TextGeneratorImpl(override val fontKey: FontKey) :
    LineSplitter<TextGeneratorImpl>(), TextGenerator {

    private val font = fontKey.toFont()
    private val metrics = getPaint(font).fontMetrics

    // top, bottom: -29.507143, 7.571181
    private val height = (metrics.bottom - metrics.top).toIntOr()

    override fun getBaselineY(): Float = -metrics.top
    override fun getLineHeight(): Float = height.toFloat()

    override fun getExampleAdvance(): Float {
        return exampleAdvanceLazy.value.toFloat()
    }

    private val exampleAdvanceLazy = lazy {
        getStringWidth(createGroup(font, "o"))
    }

    override fun getAdvance(text: CharSequence, font: TextGeneratorImpl): Float {
        return getStringWidth(createGroup(font.font, text)).toFloat()
    }

    // todo do we have/need that?
    override fun getFallbackFonts(size: Float): List<TextGeneratorImpl> = emptyList()

    override fun getSupportLevel(
        fonts: List<TextGeneratorImpl>, char: Int, lastSupportLevel: Int
    ): Int = 0

    override fun getSelfFont(): TextGeneratorImpl = this

    private fun getStringWidth(group: TextGroup) = group.offsets.last() - group.offsets.first()
    private fun createGroup(font: Font, text: CharSequence): TextGroup = TextGroup(font, text, 0.0)

    override fun calculateSize(text: CharSequence, widthLimit: Int, heightLimit: Int): Int {
        val baseWidth = getStringWidth(createGroup(font, text))
        val width = Maths.clamp(baseWidth.roundToInt() + 1, 0, GFX.maxTextureSize)
        val height = min(height, GFX.maxTextureSize)
        return GFXx2D.getSize(width, height)
    }

    override fun generateASCIITexture(
        portableImages: Boolean, callback: Callback<Texture2DArray>,
        textColor: Int, backgroundColor: Int
    ) {

        val widthLimit = GFX.maxTextureSize
        val heightLimit = GFX.maxTextureSize

        val alignment = CharacterOffsetCache.getOffsetCache(font)
        val size = alignment.getOffset('w'.code, 'w'.code)
        val width = min(widthLimit, size.roundToInt() + 1)
        val height = min(heightLimit, height)

        val texture = Texture2DArray("awtAtlas", width, height, DrawTexts.simpleChars.size)
        if (GFX.isGFXThread()) {
            createASCIITexture(
                texture, portableImages,
                textColor, backgroundColor
            )
            callback.ok(texture)
        } else {
            GPUTasks.addGPUTask("awtAtlas", width, height) {
                createASCIITexture(
                    texture, portableImages,
                    textColor, backgroundColor
                )
                callback.ok(texture)
            }
        }
    }

    private fun createASCIITexture(
        texture: Texture2DArray, portableImages: Boolean,
        textColor: Int, backgroundColor: Int
    ) {
        val bitmap = createBitmap(texture.width, texture.height * texture.layers)
        val canvas = Canvas(bitmap)
        val paint = getPaint(font)
        // fill background with that color
        paint.color = backgroundColor
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        paint.isSubpixelText = !portableImages
        var y = paint.textSize
        val x = texture.width * 0.5f
        val dy = texture.height.toFloat()
        for (yi in DrawTexts.simpleChars.lastIndex downTo 0) {
            canvas.drawText(DrawTexts.simpleChars[yi], x, y, paint)
            y += dy
        }
        val pixels = getPixels(bitmap)
        texture.createRGBA8(pixels)
    }

    private fun getPixels(bitmap: Bitmap): IntArray {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        bitmap.recycle()

        // flip vertically for upload
        val tmp = IntArray(w)
        for (y0 in 0 until (h shr 1)) {
            val y1 = h - 1 - y0
            val i0 = y0 * w
            val i1 = y1 * w
            pixels.copyInto(tmp, 0, i0, i0 + w) // tmp = y0
            pixels.copyInto(pixels, i0, i1, i1 + w) // y0 = y1
            tmp.copyInto(pixels, i1) // y1 = tmp
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            convertARGB2ABGR(pixels)
        } else {
            convertARGB2RGBA(pixels)
        }

        return pixels
    }

    override fun generateTexture(
        text: CharSequence,
        widthLimit: Int,
        heightLimit: Int,
        portableImages: Boolean,
        callback: Callback<ITexture2D>,
        textColor: Int,
        backgroundColor: Int
    ) {

        val group = createGroup(font, text)
        val width = min(widthLimit, getStringWidth(group).roundToInt() + 1)

        val lineCount = 1
        val fontHeight = height
        val height = min(heightLimit, fontHeight * lineCount)

        if (width < 1 || height < 1) return callback.err(null)
        if (max(width, height) > GFX.maxTextureSize) {
            return callback.err(
                IllegalArgumentException(
                    "Texture for text is too large! $width x $height > ${GFX.maxTextureSize}, " +
                            "${text.length} chars, $lineCount lines, ${font.name} ${font.size} px, ${
                                text.toString().shorten(200)
                            }"
                )
            )
        }

        if (text.isBlank2()) {
            // we need some kind of wrapper around texture2D
            // and return an empty/blank texture
            // that the correct size is returned is required by text input fields
            // (with whitespace at the start or end)
            return callback.ok(FakeWhiteTexture(width, height, 1))
        }

        val texture = Texture2D("awt-" + text.shorten(24), width, height, 1)
        val hasPriority = GFX.isGFXThread() && (GFX.loadTexturesSync.peek() || text.length == 1)
        if (hasPriority) {
            createImage(
                texture, portableImages,
                textColor, backgroundColor,
                text.toString()
            )
            callback.ok(texture)
        } else {
            GPUTasks.addGPUTask("awt-font-v5", width, height) {
                createImage(
                    texture, portableImages,
                    textColor, backgroundColor,
                    text.toString()
                )
                callback.ok(texture)
            }
        }
    }

    private fun createImage(
        texture: Texture2D, portableImages: Boolean,
        textColor: Int, backgroundColor: Int,
        text: String
    ) {
        val bitmap = createBitmap(texture.width, texture.height)
        val canvas = Canvas(bitmap)
        if (backgroundColor != 0) {
            // fill background with that color
            val paint = Paint()
            paint.color = backgroundColor
            canvas.drawRect(0f, 0f, texture.width.toFloat(), texture.height.toFloat(), paint)
        }
        val paint = getPaint(font)
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        paint.isSubpixelText = !portableImages
        canvas.drawText(text, texture.width * 0.5f, paint.textSize, paint)
        val pixels = getPixels(bitmap)
        texture.createRGBA(pixels, false)
    }

}
