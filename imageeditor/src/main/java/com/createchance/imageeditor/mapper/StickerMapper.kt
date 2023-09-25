package com.createchance.imageeditor.mapper

import com.volio.sticker.model.StickerModel
import com.volio.sticker.model.TextModel
import com.volio.vn.models.sticker.StickerEditModel
import com.volio.vn.models.sticker.StickerTextEditModel

fun StickerModel.mapToStickerEdit(): StickerEditModel {
    return StickerEditModel(uuid, standardSize, centerPoint, rotate, scale)
}

fun StickerEditModel.mapToSticker(): StickerModel {
    return StickerModel(uuid, standardSize, centerPoint, rotate, scale)
}

fun TextModel.mapToTextEdit(): StickerTextEditModel {
    return StickerTextEditModel(
        uuid = uuid,
        standardSize = standardSize,
        centerPoint = centerPoint,
        rotate = rotate,
        scale = scale,
        textString = textString,
        textFont = textFont,
        textColor = textColor,
        textOpacityColor = textOpacityColor,
        textSize = textSize,
        textLineSpacing = textLineSpacing,
        backgroundPadding = backgroundPadding,
        backgroundColor = backgroundColor,
        backgroundOpacityColor = backgroundOpacityColor,
        shadowColor = shadowColor,
        shadowWidth = shadowWidth,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
        textBold = textBold,
        textItalic = textItalic,
        textUnderLine = textUnderLine,
        textAlign = textAlign
    )
}

fun StickerTextEditModel.mapToText(): TextModel {
    return TextModel(
        uuid = uuid,
        standardSize = standardSize,
        centerPoint = centerPoint,
        rotate = rotate,
        scale = scale,
        textString = textString,
        textFont = textFont,
        textColor = textColor,
        textOpacityColor = textOpacityColor,
        textSize = textSize,
        textLineSpacing = textLineSpacing,
        backgroundPadding = backgroundPadding,
        backgroundColor = backgroundColor,
        backgroundOpacityColor = backgroundOpacityColor,
        shadowColor = shadowColor,
        shadowWidth = shadowWidth,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
        textBold = textBold,
        textItalic = textItalic,
        textUnderLine = textUnderLine,
        textAlign = textAlign
    )
}