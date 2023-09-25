package com.volio.vn.models.sticker

import android.graphics.Color

class StickerTextEditModel(
    uuid: String,
    standardSize: Float = 1000f,
    centerPoint: FloatArray = floatArrayOf(0.5f, 0.5f),
    rotate: Float = 0f,
    scale: Float = 1f,
    listImageUuid:MutableList<String> = mutableListOf(),
    var textString: String = "Please tell",
    var textFont: String = "",
    var textColor: Int = Color.RED,
    var textOpacityColor: Float = 1f,

    var textSize: Float = 30f,
    var textLineSpacing: Float = 0f,

    var backgroundPadding: Float = 10f,
    var backgroundColor: Int = Color.TRANSPARENT,
    var backgroundOpacityColor: Float = 1f,

    var shadowColor: Int = Color.TRANSPARENT,
    var shadowWidth: Float = 0f,
    var strokeWidth: Float = 0f,
    var strokeColor: Int = Color.TRANSPARENT,

    var textBold: Boolean = false,
    var textItalic: Boolean = false,
    var textUnderLine: Boolean = false,
    var textAlign: Int = CENTER,

    ) : StickerEditModel(uuid, standardSize, centerPoint, rotate, scale,listImageUuid) {
    override fun setData(stickerModel: StickerEditModel) {
        setCenterPoint(stickerModel.getCenterX(), stickerModel.getCenterY())
        rotate = stickerModel.rotate
        scale = stickerModel.scale
        if (stickerModel is StickerTextEditModel) {
            textString = stickerModel.textString
            textSize = stickerModel.textSize
            textLineSpacing = stickerModel.textLineSpacing
            textColor = stickerModel.textColor
            backgroundPadding = stickerModel.backgroundPadding
            backgroundColor = stickerModel.backgroundColor
            shadowColor = stickerModel.shadowColor
            shadowWidth = stickerModel.shadowWidth
            strokeWidth = stickerModel.strokeWidth
            strokeColor = stickerModel.strokeColor
        }
    }

    override fun copy(): StickerEditModel {
        val listImage:MutableList<String> = mutableListOf()
        listImage.addAll(listImageUuid)
        return StickerTextEditModel(
            uuid,
            standardSize,
            centerPoint,
            rotate,
            scale,
            listImage,
            textString,
            textFont,
            textColor,
            textOpacityColor,
            textSize,
            textLineSpacing,
            backgroundPadding,
            backgroundColor,
            backgroundOpacityColor,
            shadowColor,
            shadowWidth,
            strokeWidth,
            strokeColor,
            textBold,
            textItalic,
            textUnderLine,
            textAlign,
        )
    }

    companion object {
        const val LEFT = 1
        const val CENTER = 2
        const val RIGHT = 3
        const val TOP = 4
    }
}