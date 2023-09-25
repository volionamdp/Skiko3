package com.volio.vn.models.music

import android.os.Parcelable
import com.volio.vn.models.category.CategoryModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicCategoryModel(
    override val id: Long,
    override val name: String
) : CategoryModel, Parcelable
