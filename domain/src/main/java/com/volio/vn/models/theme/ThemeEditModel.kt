package com.volio.vn.models.theme

import com.volio.vn.models.RemoteData

data class ThemeEditModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override val localPath: String,
    override val thumb: String
) : RemoteData {
    companion object {
        operator fun invoke(themeModel: ThemeModel, intensity: Float): ThemeEditModel {
            return ThemeEditModel(
                id = themeModel.id,
                idCategory = themeModel.idCategory,
                name = themeModel.name,
                remotePath = themeModel.remotePath,
                localPath = themeModel.localPath,
                thumb = themeModel.thumb
            )
        }
    }
}
