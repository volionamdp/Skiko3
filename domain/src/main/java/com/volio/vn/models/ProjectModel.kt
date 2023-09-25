package com.volio.vn.models

import android.os.Parcelable
import com.volio.vn.models.frame.FrameModel
import com.volio.vn.models.music.ProjectMusicModel
import com.volio.vn.models.photo.PhotoEditModel
import com.volio.vn.models.sticker.StickerEditModel
import com.volio.vn.models.theme.ThemeModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ProjectModel(
    var id: Long,
    var nameProject: String,
    var timeModify: Long,
    var timeCreate: Long,
    var pathSave: String = "",
    var listPhoto: @RawValue MutableList<PhotoEditModel>,
    var ratioModel: @RawValue RatioModel,
    var prjMusicModel: @RawValue ProjectMusicModel? = null,
    var prjFrameModel: @RawValue FrameModel? = null,
    var theme: @RawValue ThemeModel? = null,
    var listSticker: @RawValue MutableList<StickerEditModel>? = null
) : Parcelable {
    fun copy(): ProjectModel {
        val listPhotoModel: MutableList<PhotoEditModel> = mutableListOf()
        val listStickerModel: MutableList<StickerEditModel> = mutableListOf()
        listPhoto.forEach {
            listPhotoModel.add(it.copy())
        }
        listSticker?.forEach {
            listStickerModel.add(it.copy())
        }
        return ProjectModel(
            id = id,
            nameProject = nameProject,
            timeModify = timeModify,
            timeCreate = timeCreate,
            pathSave = pathSave,
            listPhoto = listPhotoModel,
            ratioModel = ratioModel,
            prjMusicModel = prjMusicModel,
            prjFrameModel = prjFrameModel,
            theme = theme,
            listSticker = listStickerModel
        )
    }
}
