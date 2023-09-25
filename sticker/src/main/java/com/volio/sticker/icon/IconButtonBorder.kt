package com.volio.sticker.icon

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.SizeF
import android.view.MotionEvent
import com.volio.sticker.CornerGravity
import com.volio.sticker.StickerView
import com.volio.sticker.dp
import com.volio.sticker.model.StickerModel

class IconButtonBorder(
    drawable: Drawable?,
    cornerGravity: CornerGravity,
    size: SizeF,
    val onClick: (StickerModel) -> Unit
) :
    IconButton(drawable, cornerGravity, size) {
    private var paddingHorizontal = 6.dp
    private var paddingVertical = 3.dp
    private var text: String = ""
    private var marginText = 3.dp
    private var textWidthSpace = 0f
    private var paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }
    private var paintBackgroundBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 0.1f.dp
        style = Paint.Style.STROKE
    }

    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 12.dp
        color = Color.WHITE
    }

    fun setText(textContent: String) {
        text = textContent
        textWidthSpace = if (text.isNotEmpty()) {
            textPaint.measureText(text) + marginText
        } else {
            0f
        }

    }

    override fun drawIcon(canvas: Canvas) {
        canvas.drawRoundRect(
            -paddingHorizontal,
            -paddingVertical,
            size.width + paddingHorizontal + textWidthSpace,
            size.height + paddingVertical,
            1000f,
            1000f,
            paintBackground
        )
        canvas.drawRoundRect(
            -paddingHorizontal,
            -paddingVertical,
            size.width + paddingHorizontal + textWidthSpace,
            size.height + paddingVertical,
            1000f,
            1000f,
            paintBackgroundBorder
        )
        canvas.drawText(
            text,
            size.width + marginText,
            size.height / 2 + textPaint.textSize / 3,
            textPaint
        )
        super.drawIcon(canvas)
    }

    override fun contains(x: Float, y: Float, padding: Float): Boolean {
        val floatArray = floatArrayOf(x, y)
        invertMatrix.mapPoints(floatArray)
        return floatArray[0] >= -padding - paddingHorizontal && floatArray[0] < size.width + padding + textWidthSpace + paddingHorizontal &&
                floatArray[1] >= -padding + paddingVertical && floatArray[1] < size.height + padding + paddingVertical
    }

    override fun onActionDown(stickerView: StickerView?, event: MotionEvent) {
        super.onActionDown(stickerView, event)
        stickerView?.getCurrentSticker()?.let {
            onClick(it.getStickerData())
        }
    }


}