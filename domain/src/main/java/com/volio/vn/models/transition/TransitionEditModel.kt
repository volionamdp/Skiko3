package com.volio.vn.models.transition

import android.os.Parcelable
import com.volio.vn.models.RemoteData
import kotlinx.android.parcel.Parcelize

@Parcelize
open class TransitionEditModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override val localPath: String,
    override val thumb: String,
    var duration: Long,
    var isAsset: Boolean = false
) : RemoteData, Parcelable {
    companion object {
        operator fun invoke(
            transitionModel: TransitionModel,
            isGLSl: Boolean = false
        ): TransitionEditModel {
            if (isGLSl) {
                return TransitionGLSLEditModel(
                    transId = transitionModel.id.toInt(),
                    id = transitionModel.id,
                    idCategory = transitionModel.idCategory,
                    name = transitionModel.name,
                    remotePath = transitionModel.remotePath,
                    localPath = transitionModel.localPath,
                    thumb = transitionModel.thumb,
                    duration = transitionModel.duration,
                )
            }
            return TransitionPagOverlayEditModel(
                id = transitionModel.id,
                idCategory = transitionModel.idCategory,
                name = transitionModel.name,
                remotePath = transitionModel.remotePath,
                localPath = transitionModel.localPath,
                thumb = transitionModel.thumb,
                duration = transitionModel.duration,
                isAsset = transitionModel.isAsset

            )
        }

        fun getNoneItem(): TransitionEditModel {
            return TransitionEditModel(-1, -1, "", "", "", "", 0, false)
        }
    }

}
