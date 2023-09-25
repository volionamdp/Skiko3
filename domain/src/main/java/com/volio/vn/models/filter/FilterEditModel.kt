package com.volio.vn.models.filter

import android.os.Parcelable
import com.volio.vn.models.RemoteData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterEditModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override val localPath: String,
    override val thumb: String,
    var intensity: Float
) : RemoteData, Parcelable {
    companion object {
        operator fun invoke(filterModel: FilterModel, intensity: Float): FilterEditModel {
            return FilterEditModel(
                id = filterModel.id,
                idCategory = filterModel.idCategory,
                name = filterModel.name,
                remotePath = filterModel.remotePath,
                localPath = filterModel.localPath,
                thumb = filterModel.thumb,
                intensity = intensity
            )
        }
    }
}
