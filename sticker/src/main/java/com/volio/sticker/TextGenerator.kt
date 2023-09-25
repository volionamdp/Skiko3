package com.volio.sticker

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.volio.sticker.model.TextModel
import com.volio.sticker.model.TextStaticModel
import kotlin.math.min
import kotlin.math.roundToInt

object TextGenerator {
    private const val textStandardSizeMax: Float = 200f
    private const val textStandardSizeMin: Float = 20f

    fun createText(
        context: Context?,
        scWidth: Float,
        scHeight: Float,
        textModel: TextModel
    ): Bitmap {
        val paintText = TextPaint()
        paintText.typeface = getFont(context,textModel)
        val result = calculateTextRect(scWidth, scHeight, textModel, paintText)
        val bitmap = Bitmap.createBitmap(
            (result.width + textModel.backgroundPadding * 2).toInt(),
            (result.height + textModel.backgroundPadding * 2).toInt(),
            Bitmap.Config.ARGB_8888
        )
        Canvas(bitmap).apply {
            translate(textModel.backgroundPadding, textModel.backgroundPadding)
            Paint().let {
                setBackgroundPaint(it, textModel)
                drawPaint(it)
            }
            setDefaultText(paintText, textModel)
            setShadowText(paintText, textModel, true)
            result.staticLayout.draw(this)
            if (textModel.strokeWidth > 0) {
                setStrokeText(paintText, textModel)
                setShadowText(paintText, textModel, false)
                result.staticLayout.draw(this)
            }
        }
        return bitmap
    }

    // result StaticLayout,Width,Height
    fun calculateTextRect(
        scWidth: Float,
        scHeight: Float,
        textModel: TextModel,
        textPaint: TextPaint,
    ): TextStaticModel {
        val sizeScale = min(scWidth, scHeight) / textModel.standardSize
        val textSize =
            (textStandardSizeMin + (textModel.textSize * (textStandardSizeMax - textStandardSizeMin) / 100f)) * sizeScale
        textPaint.textSize = textSize

        val width = textModel.textString.split("\n").maxOf { textPaint.measureText(it) }
        val staticLayout = StaticLayout.Builder.obtain(
            textModel.textString,
            0,
            textModel.textString.length,
            textPaint,
            width.roundToInt()
        )
            .setLineSpacing(textSize * textModel.textLineSpacing / 100, 1f)
            .setAlignment(getAlignment(textModel))
            .build()
        val height = staticLayout.height
        return TextStaticModel(staticLayout, width.roundToInt(), height)
    }

    private fun getAlignment(textModel: TextModel): Layout.Alignment {
        return when (textModel.textAlign) {
            TextModel.LEFT -> {
                Layout.Alignment.ALIGN_NORMAL
            }
            TextModel.RIGHT -> {
                Layout.Alignment.ALIGN_OPPOSITE
            }
            else -> {
                Layout.Alignment.ALIGN_CENTER
            }
        }
    }

    fun setBackgroundPaint(backgroundPaint: Paint, textModel: TextModel) {
        backgroundPaint.color = textModel.backgroundColor
        if (textModel.backgroundColor != Color.TRANSPARENT) {
            backgroundPaint.alpha = ((textModel.backgroundOpacityColor * 255).toInt())
        }
    }

    fun setShadowText(textPaint: TextPaint, textModel: TextModel, isShadow: Boolean) {
        if (isShadow && textModel.shadowWidth > 0) {
            textPaint.setShadowLayer(textModel.shadowWidth, 0f, 0f, textModel.shadowColor)
        } else {
            textPaint.clearShadowLayer()
        }
    }

    fun setStrokeText(textPaint: TextPaint, textModel: TextModel) {
        textPaint.style = Paint.Style.STROKE
        textPaint.strokeWidth = textModel.strokeWidth
        textPaint.color = textModel.strokeColor
        textPaint.alpha = ((textModel.textOpacityColor * 255).toInt())
    }

    fun setDefaultText(textPaint: TextPaint, textModel: TextModel) {
        textPaint.style = Paint.Style.FILL
        textPaint.color = textModel.textColor
        textPaint.alpha = ((textModel.textOpacityColor * 255).toInt())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textPaint.underlineColor = textModel.textColor
        }
    }

    fun getFont(context: Context?, textModel: TextModel): Typeface? {
        if (textModel.textFont.isNotEmpty()) {
            if (textModel.textFont.contains("file:///android_asset/")) {
                context?.let {
                    return Typeface.createFromAsset(
                        it.assets,
                        textModel.textFont.replace("file:///android_asset/", "")
                    )
                }
            }
        }
        return null
    }

}