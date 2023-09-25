package com.volio.sticker.model

open class StickerModel(
    val uuid: String,
    val standardSize: Float = 1000f,
    val centerPoint: FloatArray = floatArrayOf(0.5f, 0.5f),
    var rotate: Float,
    var scale: Float,
) {
    fun getCenterX() = centerPoint[0]
    fun getCenterY() = centerPoint[1]
    fun setCenterPoint(x: Float, y: Float) {
        centerPoint[0] = x
        centerPoint[1] = y
    }

    open fun setData(stickerModel: StickerModel) {
        setCenterPoint(stickerModel.getCenterX(), stickerModel.getCenterY())
        rotate = stickerModel.rotate
        scale = stickerModel.scale
    }
}
