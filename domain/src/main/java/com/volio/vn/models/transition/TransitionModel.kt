package com.volio.vn.models.transition

import com.volio.vn.models.RemoteData

data class TransitionModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String,
    override var localPath: String,
    override val thumb: String,
    var isAsset: Boolean,
    var duration: Long,
) : RemoteData
