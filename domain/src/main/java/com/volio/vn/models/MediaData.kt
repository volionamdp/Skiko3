package com.volio.vn.models

import android.os.Parcelable

interface MediaData: Parcelable {
    val id: Long
    val name: String
    val uri: String
    var path: String
    val timeCreated: Long
    val idFolder: Long
    var uuid: String
}
