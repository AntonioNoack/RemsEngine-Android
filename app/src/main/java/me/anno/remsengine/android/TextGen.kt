package me.anno.remsengine.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import me.anno.fonts.Font
import me.anno.fonts.FontManager
import me.anno.fonts.FontStats
import me.anno.fonts.TextGenerator
import me.anno.fonts.TextGroup
import me.anno.fonts.keys.FontKey
import me.anno.fonts.mesh.CharacterOffsetCache
import me.anno.gpu.GFX
import me.anno.gpu.drawing.DrawTexts
import me.anno.gpu.drawing.GFXx2D
import me.anno.gpu.texture.FakeWhiteTexture
import me.anno.gpu.texture.ITexture2D
import me.anno.gpu.texture.Texture2D
import me.anno.gpu.texture.Texture2DArray
import me.anno.maths.Maths
import me.anno.remsengine.android.AndroidPlugin.getPaint
import me.anno.utils.strings.StringHelper.shorten
import me.anno.utils.types.Strings.isBlank2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class TextGen(val key: FontKey) : TextGenerator {

    val font = Font(key.name, FontManager.getAvgFontSize(key.sizeIndex), key.bold, key.italic)
    val height = FontStats.getFontHeight(font).toInt()

    private fun getStringWidth(group: TextGroup) = group.offsets.last() - group.offsets.first()
    private fun createGroup(font: Font, text: CharSequence): TextGroup = TextGroup(font, text, 0.0)

    override fun calculateSize(
        text: CharSequence,
        widthLimit: Int,
        heightLimit: Int
    ): Int {

        // todo do we need to handle emojis separately?
        val baseWidth = getStringWidth(createGroup(font, text))
        val width = Maths.clamp(baseWidth.roundToInt() + 1, 0, GFX.maxTextureSize)
        val height = min(height, GFX.maxTextureSize)
        return GFXx2D.getSize(width, height)
    }

    override fun generateASCIITexture(
        portableImages: Boolean,
        textColor: Int,
        backgroundColor: Int,
        extraPadding: Int
    ): Texture2DArray {

        val widthLimit = GFX.maxTextureSize
        val heightLimit = GFX.maxTextureSize

        val alignment = CharacterOffsetCache.getOffsetCache(font)
        val size = alignment.getOffset('w'.code, 'w'.code)
        val width = min(widthLimit, size.roundToInt() + 1 + 2 * extraPadding)
        val height = min(heightLimit, height + 2 * extraPadding)

        val texture = Texture2DArray("awtAtlas", width, height, DrawTexts.simpleChars.size)
        if (GFX.isGFXThread()) {
            createASCIITexture(
                texture, portableImages,
                textColor, backgroundColor, extraPadding
            )
        } else {
            GFX.addGPUTask("awtAtlas", width, height) {
                createASCIITexture(
                    texture, portableImages,
                    textColor, backgroundColor, extraPadding
                )
            }
        }

        return texture
    }

    private fun createASCIITexture(
        texture: Texture2DArray,
        portableImages: Boolean,
        textColor: Int,
        backgroundColor: Int,
        extraPadding: Int
    ) {
        val bitmap = Bitmap.createBitmap(
            texture.width,
            texture.height * texture.layers,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = getPaint(font)
        // fill background with that color
        paint.color = backgroundColor
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        var y = extraPadding + paint.textSize
        val x = texture.width * 0.5f
        val dy = texture.height.toFloat()
        for (yi in DrawTexts.simpleChars.indices) {
            canvas.drawText(DrawTexts.simpleChars[yi], x, y, paint)
            y += dy
        }
        val pixels = getPixels(bitmap)
        texture.createRGBA8(pixels)
    }

    private fun getPixels(bitmap: Bitmap): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        bitmap.recycle()
        return pixels
    }

    override fun generateTexture(
        text: CharSequence,
        widthLimit: Int,
        heightLimit: Int,
        portableImages: Boolean,
        textColor: Int,
        backgroundColor: Int,
        extraPadding: Int
    ): ITexture2D? {

        val group = createGroup(font, text)
        val width = min(widthLimit, getStringWidth(group).roundToInt() + 1 + 2 * extraPadding)

        val lineCount = 1
        val fontHeight = height
        val height = min(heightLimit, fontHeight * lineCount + 2 * extraPadding)

        if (width < 1 || height < 1) return null
        if (max(width, height) > GFX.maxTextureSize) {
            IllegalArgumentException(
                "Texture for text is too large! $width x $height > ${GFX.maxTextureSize}, " +
                        "${text.length} chars, $lineCount lines, ${font.name} ${font.size} px, ${
                            text.toString().shorten(200)
                        }"
            ).printStackTrace()
            return null
        }

        if (text.isBlank2()) {
            // we need some kind of wrapper around texture2D
            // and return an empty/blank texture
            // that the correct size is returned is required by text input fields
            // (with whitespace at the start or end)
            return FakeWhiteTexture(width, height, 1)
        }

        val texture = Texture2D("awt-" + text.shorten(24), width, height, 1)
        val hasPriority = GFX.isGFXThread() && (GFX.loadTexturesSync.peek() || text.length == 1)
        if (hasPriority) {
            createImage(texture, textColor, backgroundColor, extraPadding, text.toString())
        } else {
            GFX.addGPUTask("awt-font-v5", width, height) {
                createImage(texture, textColor, backgroundColor, extraPadding, text.toString())
            }
        }

        return texture
    }

    private fun createImage(
        texture: Texture2D, textColor: Int, backgroundColor: Int,
        extraPadding: Int, text: String
    ) {
        val bitmap = Bitmap.createBitmap(texture.width, texture.height, Bitmap.Config.ARGB_8888)
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
        canvas.drawText(text, texture.width * 0.5f, extraPadding + paint.textSize, paint)
        val pixels = getPixels(bitmap)
        texture.createRGBA(pixels, false)
    }

}
