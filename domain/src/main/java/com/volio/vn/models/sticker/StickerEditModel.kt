package com.volio.vn.models.sticker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class StickerEditModel(
    val uuid: String,
    val standardSize: Float = 1000f,
    val centerPoint: FloatArray = floatArrayOf(0.5f, 0.5f),
    var rotate: Float,
    var scale: Float,
    val listImageUuid:MutableList<String> = mutableListOf()
) : Parcelable {
    fun getCenterX() = centerPoint[0]
    fun getCenterY() = centerPoint[1]
    fun setCenterPoint(x: Float, y: Float) {
        centerPoint[0] = x
        centerPoint[1] = y
    }
    fun contains(imageUuid:String):Boolean{
        listImageUuid.forEach {
            if (it==imageUuid){
                return true
            }
        }
        return false
    }
    open fun setData(stickerModel: StickerEditModel) {
        setCenterPoint(stickerModel.getCenterX(), stickerModel.getCenterY())
        rotate = stickerModel.rotate
        scale = stickerModel.scale
    }

    open fun copy(): StickerEditModel {
        return StickerEditModel(uuid, standardSize, centerPoint, rotate, scale, listImageUuid)
    }

    companion object {

    }
}
