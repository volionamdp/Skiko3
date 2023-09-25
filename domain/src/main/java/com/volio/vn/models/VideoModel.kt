package com.volio.vn.models

import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoModel(
    override val id: Long,
    override var name: String,
    override val uri: String,
    override var path: String,
    override var timeCreated: Long,
    override val idFolder: Long,
    val duration: Long,
    override var uuid: String = "",
) : MediaData