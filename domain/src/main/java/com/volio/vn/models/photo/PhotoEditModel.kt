package com.volio.vn.models.photo

import com.volio.vn.models.MediaData
import com.volio.vn.models.background.BackgroundEditBlurModel
import com.volio.vn.models.background.BackgroundEditModel
import com.volio.vn.models.background.BackgroundEditModel.Companion.DEFAULT_BLUR_LV
import com.volio.vn.models.filter.FilterEditModel
import com.volio.vn.models.font.TextEditModel
import com.volio.vn.models.frame.FrameEditModel
import com.volio.vn.models.sticker.StickerEditModel
import com.volio.vn.models.transition.TransitionEditModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PhotoEditModel(
    override var id: Long,
    override var name: String,
    override var uri: String,
    override var path: String,
    override var timeCreated: Long,
    override var idFolder: Long,
    override var uuid: String,
    var duration: Long,
    var isFilePhoto: Boolean,
    var background: @RawValue BackgroundEditModel,
    var transition: @RawValue TransitionEditModel?,
//    var sticker: @RawValue List<StickerEditModel>?,
    var filter: @RawValue FilterEditModel?,
    var text: @RawValue List<TextEditModel>?,
    var crop: @RawValue CropModel?
) : MediaData {

    fun setData(mediaData: MediaData) {
        this.id = mediaData.id
        this.uuid = mediaData.uuid
        this.name = mediaData.name
        this.timeCreated = mediaData.timeCreated
        this.idFolder = mediaData.idFolder
        if (mediaData.path != this.path || mediaData.uri != this.uri) {
            this.path = mediaData.path
            this.uri = mediaData.uri
        }
    }

    fun toPhotoModel(): PhotoModel {
        return PhotoModel(
            id = this.id,
            name = this.name,
            uri = this.uri,
            path = this.path,
            timeCreated = this.timeCreated,
            idFolder = this.idFolder,
            uuid = this.uuid,
            duration = this.duration,
            durationTransition = this.transition?.duration ?: 0,
        )
    }

    fun toTemplatePhotoModel(): PhotoTemplateModel {
        return PhotoTemplateModel(
            id = this.id,
            name = this.name,
            uri = this.uri,
            path = this.path,
            timeCreated = this.timeCreated,
            idFolder = this.idFolder,
            uuid = this.uuid,
            remotePath = "",
            thumb = this.path,
            duration = this.duration,
            durationTransition = this.transition?.duration ?: 0,
        )
    }

    fun copy(): PhotoEditModel {
        val background = this.background.copy()
        return this.copy(background = background)
    }

    companion object {
        fun create(mediaData: MediaData, isFilePhoto: Boolean): PhotoEditModel {
            return PhotoEditModel(
                mediaData.id,
                mediaData.name,
                mediaData.uri,
                mediaData.path,
                mediaData.timeCreated,
                mediaData.idFolder,
                mediaData.uuid,
                if (mediaData is PhotoModel) mediaData.duration else if (mediaData is PhotoTemplateModel) mediaData.duration else 3000,
                isFilePhoto,
                BackgroundEditBlurModel(DEFAULT_BLUR_LV),
                TransitionEditModel.getNoneItem(),
//                emptyList(),
                null,
                null, null
            )
        }
    }
}
