package com.volio.vn.repositories

import com.volio.vn.models.frame.FrameCategoryModel
import com.volio.vn.models.frame.FrameModel
import kotlinx.coroutines.flow.Flow

interface FrameRepository {
    suspend fun getAllFrameCategory(isFetch: Boolean): Flow<DataState<List<FrameCategoryModel>>>
    suspend fun getFrameByCategoryId(isFetch: Boolean,categoryId: Long): Flow<DataState<List<FrameModel>>>
    fun downloadFrameZipRatio(frameModel: FrameModel,onDone: (Boolean) ->Unit)
}