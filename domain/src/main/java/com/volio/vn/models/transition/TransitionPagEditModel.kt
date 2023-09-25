package com.volio.vn.models.transition

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

open class TransitionPagEditModel(
    id: Long,
    idCategory: Long,
    name: String,
    remotePath: String,
    localPath: String,
    thumb: String,
    duration: Long,
    isAsset: Boolean = false
) : TransitionEditModel(id, idCategory, name, remotePath, localPath, thumb, duration, isAsset){
    companion object {
        operator fun invoke(
            transitionModel: TransitionModel,
            duration: Long
        ): TransitionPagEditModel {
            return TransitionPagEditModel(
                id = transitionModel.id,
                idCategory = transitionModel.idCategory,
                name = transitionModel.name,
                remotePath = transitionModel.remotePath,
                localPath = transitionModel.localPath,
                thumb = transitionModel.thumb,
                duration = duration
            )
        }
    }
}
