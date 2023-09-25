package com.volio.vn.models.transition

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class TransitionPagOverlayEditModel(
    id: Long,
    idCategory: Long,
    name: String,
    remotePath: String,
    localPath: String,
    thumb: String,
    duration: Long,
    isAsset: Boolean
) : TransitionPagEditModel(id, idCategory, name, remotePath, localPath, thumb, duration, isAsset){
}