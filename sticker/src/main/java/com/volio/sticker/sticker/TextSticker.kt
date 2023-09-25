package com.volio.sticker.sticker

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.volio.sticker.TextGenerator
import com.volio.sticker.model.StickerModel
import com.volio.sticker.model.TextModel
import java.util.*

class TextSticker() : Sticker() {
    private var paintText = TextPaint()
    private var paintBackground = Paint()
    private var staticLayout: StaticLayout =
        StaticLayout.Builder.obtain("", 0, 0, paintText, 200).build()
    override var data: StickerModel = TextModel(UUID.randomUUID().toString())
    private var lastKeyFont: String = ""
    private var lastKeyText: String = ""
    private var typeface: Typeface? = null
    var dataText: TextModel
        get() = data as TextModel
        set(value) {
            data = value
            setTextFont()
            calculateRect()

        }


    init {
        paintText.color = dataText.textColor
        paintBackground.color = dataText.backgroundColor
    }

    override fun updateScreenSize(width: Float, height: Float) {
        super.updateScreenSize(width, height)
        dataText = dataText
    }


    fun setTextSize(size: Float) {
        dataText.textSize = size
        calculateRect()
    }

    fun setTextLineSpacing(size: Float) {
        dataText.textLineSpacing = size
        calculateRect()
    }

    fun setTextColor(color: Int) {
        dataText.textColor = color
        paintText.color = color
        stickerUpdate?.onUpdateView()
    }

    fun setText(text: String) {
        dataText.textString = text
        calculateRect()
    }

    fun setShadowText(color: Int, width: Float) {
        dataText.shadowColor = color
        dataText.shadowWidth = width
        stickerUpdate?.onUpdateView()
    }

    fun setStrokeText(color: Int, width: Float) {
        dataText.strokeColor = color
        dataText.strokeWidth = width
        stickerUpdate?.onUpdateView()
    }

    private fun setTextFont() {
        paintText.isFakeBoldText = dataText.textBold
        paintText.textSkewX = if (dataText.textItalic) -0.25f else 0f
        paintText.isUnderlineText = dataText.textUnderLine
        paintText.typeface = getFont()
    }

    private fun getFont(): Typeface? {
        Log.d(
            "Veeet",
            "getFont: ${dataText.textFont} ${lastKeyFont != dataText.generatorKeyFont()}"
        )
        if (dataText.textFont.isNotEmpty() && (lastKeyFont != dataText.generatorKeyFont() || typeface == null)) {
            lastKeyFont = dataText.generatorKeyFont()
            if (dataText.textFont.contains("file:///android_asset/")) {
                context?.let {
                    typeface = Typeface.createFromAsset(
                        it.assets,
                        dataText.textFont.replace("file:///android_asset/", "")
                    )
                }
            }
        }
        return typeface
    }

    private fun calculateRect() {
        if (scWidth > 0 && scHeight > 0) {
            if (lastKeyText != dataText.generatorTextKey()) {
                lastKeyText = dataText.generatorTextKey()
                val result = TextGenerator.calculateTextRect(scWidth, scHeight, dataText, paintText)
                staticLayout = result.staticLayout
                rectSrc.set(0f, 0f, result.width.toFloat(), result.height.toFloat())
            }
            updateMatrix()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.setMatrix(matrix)
        TextGenerator.setBackgroundPaint(paintBackground, dataText)
        canvas.drawRect(
            -getBackgroundPadding(),
            -getBackgroundPadding(),
            rectSrc.width() + getBackgroundPadding(),
            rectSrc.height() + getBackgroundPadding(),
            paintBackground
        )
        TextGenerator.setDefaultText(paintText, dataText)
        TextGenerator.setShadowText(paintText, dataText, true)
        staticLayout.draw(canvas)
        if (dataText.strokeWidth > 0) {
            TextGenerator.setStrokeText(paintText, dataText)
            TextGenerator.setShadowText(paintText, dataText, false)
            staticLayout.draw(canvas)
        }
        canvas.restore()
    }

//    private fun setBackgroundPaint(backgroundPaint: Paint,textModel: TextModel) {
//        backgroundPaint.color = textModel.backgroundColor
//        if (textModel.backgroundColor != Color.TRANSPARENT) {
//            backgroundPaint.alpha = ((textModel.backgroundOpacityColor * 255).toInt())
//        }
//    }
//
//    private fun setShadowText(textPaint: TextPaint,textModel: TextModel,isShadow: Boolean) {
//        if (isShadow && textModel.shadowWidth > 0) {
//            textPaint.setShadowLayer(textModel.shadowWidth, 0f, 0f, textModel.shadowColor)
//        } else {
//            textPaint.clearShadowLayer()
//        }
//    }
//
//    private fun setStrokeText(textPaint: TextPaint,textModel: TextModel) {
//        textPaint.style = Paint.Style.STROKE
//        textPaint.strokeWidth = textModel.strokeWidth
//        textPaint.color = textModel.strokeColor
//        textPaint.alpha = ((textModel.textOpacityColor * 255).toInt())
//    }
//
//    private fun setDefaultText(textPaint: TextPaint,textModel: TextModel) {
//        textPaint.style = Paint.Style.FILL
//        textPaint.color = textModel.textColor
//        textPaint.alpha = ((textModel.textOpacityColor * 255).toInt())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            textPaint.underlineColor = textModel.textColor
//        }
//    }

    override fun getSrcPaddingBorder(): Float {
        return padding / dataText.scale + getBackgroundPadding()
    }

    private fun getBackgroundPadding() = dataText.backgroundPadding
    override fun getContentWidth(): Float {
        return getBackgroundPadding() * 2 + staticLayout.width
    }

//    fun getTextModel(): TextModel {
//        return TextModel(
//            textContent,
//            standardSize,
//            floatArrayOf(centerPoint.x, centerPoint.y),
//            rotate,
//            scale,
//            textSize,
//            textColor,
//            backgroundPadding,
//            backgroundColor,
//            shadowColor,
//            shadowWidth,
//            strokeWidth,
//            strokeColor
//        )
//    }
//
//    fun setTextModel(textModel: TextModel) {
//        textContent = textModel.textString
//        standardSize = textModel.standardSize
//        centerPoint.set(textModel.centerPoint[0], textModel.centerPoint[1])
//        rotate = textModel.rotate
//
//    }
}