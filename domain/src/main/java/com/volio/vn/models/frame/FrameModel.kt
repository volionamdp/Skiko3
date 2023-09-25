package com.volio.vn.models.frame

import android.os.Parcelable
import com.volio.vn.models.RemoteData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FrameModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override var localPath: String,
    override val thumb: String
) : RemoteData, Parcelable
