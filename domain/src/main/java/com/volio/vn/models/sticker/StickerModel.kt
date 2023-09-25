package com.volio.vn.models.sticker

import android.os.Parcelable
import com.volio.vn.models.RemoteData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickerModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override val localPath: String,
    override val thumb: String
) : RemoteData, Parcelable
