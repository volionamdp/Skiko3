package com.volio.vn.models.background

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BackgroundEditImageModel(val blurLevel2: Int, var localPath: String, var isAsset: Boolean) :
    BackgroundEditModel(blurLevel2),
    Parcelable {
    override fun copy(): BackgroundEditImageModel {
        return BackgroundEditImageModel(this.blurLevel, this.localPath, this.isAsset)
    }
}