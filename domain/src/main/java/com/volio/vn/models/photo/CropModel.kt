package com.volio.vn.models.photo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CropModel(
    val srcBitmapWidth: Int,
    val srcBitmapHeight: Int,
    val points: FloatArray,
    val degreesRotated: Int = 0,
    val fixAspectRatio: Boolean,
    val aspectRatioX: Int,
    val aspectRatioY: Int,
    val flipHorizontally: Boolean = false,
    val flipVertically: Boolean = false,
) : Parcelable {
    override fun toString(): String {
        return "CropModel(srcBitmapWidth=$srcBitmapWidth, srcBitmapHeight=$srcBitmapHeight, points=${points.contentToString()}, degreesRotated=$degreesRotated, fixAspectRatio=$fixAspectRatio, aspectRatioX=$aspectRatioX, aspectRatioY=$aspectRatioY, flipHorizontally=$flipHorizontally, flipVertically=$flipVertically)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CropModel
        if (srcBitmapWidth != other.srcBitmapWidth) return false
        if (srcBitmapHeight != other.srcBitmapHeight) return false
        if (!points.contentEquals(other.points)) return false
        if (degreesRotated != other.degreesRotated) return false
        if (fixAspectRatio != other.fixAspectRatio) return false
        if (aspectRatioX != other.aspectRatioX) return false
        if (aspectRatioY != other.aspectRatioY) return false
        if (flipHorizontally != other.flipHorizontally) return false
        if (flipVertically != other.flipVertically) return false
        return true
    }

}
