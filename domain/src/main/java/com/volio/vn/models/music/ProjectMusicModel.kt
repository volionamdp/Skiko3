package com.volio.vn.models.music

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectMusicModel(
    val musicModel: MusicModel,
    val startTime: Long,
    var endTime: Long
): Parcelable