package com.volio.vn.models.background

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
open class BackgroundEditModel(
    var blurLevel: Int,
): Parcelable {
    open fun copy():BackgroundEditModel {
        return BackgroundEditModel(this.blurLevel)
    }

    companion object {
        const val DEFAULT_BLUR_LV = 75
    }
}