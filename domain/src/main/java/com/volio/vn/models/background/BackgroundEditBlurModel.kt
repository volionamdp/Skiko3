package com.volio.vn.models.background

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BackgroundEditBlurModel(val blurLevel2:Int): BackgroundEditModel(blurLevel2){
    override fun copy():BackgroundEditBlurModel{
        return BackgroundEditBlurModel(this.blurLevel)
    }
}
