package com.volio.vn.models.photo

import com.volio.vn.models.MediaData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoModel(
    override val id: Long,
    override val name: String,
    override val uri: String,
    override var path: String,
    override val timeCreated: Long,
    override val idFolder: Long,
    override var uuid: String = "",
    var duration: Long = 3000L,
    var durationTransition: Long = 0L,
) : MediaData