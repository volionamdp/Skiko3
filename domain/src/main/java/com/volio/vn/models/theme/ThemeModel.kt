package com.volio.vn.models.theme

import com.volio.vn.models.RemoteData

data class ThemeModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override var localPath: String,
    override val thumb: String,
    val updatedAt: String
) : RemoteData
