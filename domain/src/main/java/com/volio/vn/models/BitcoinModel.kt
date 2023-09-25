package com.volio.vn.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BitcoinModel(
    val date: String,
    val price: Double,
    val volume: String,
) : Parcelable
