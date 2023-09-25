package com.volio.vn.models.filter

import com.volio.vn.models.RemoteData

data class FilterModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override val localPath: String,
    override val thumb: String
) : RemoteData
