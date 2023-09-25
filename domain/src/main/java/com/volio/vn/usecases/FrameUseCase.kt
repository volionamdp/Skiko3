package com.volio.vn.usecases

import com.volio.vn.models.frame.FrameCategoryModel
import com.volio.vn.models.frame.FrameModel
import com.volio.vn.repositories.DataState
import com.volio.vn.repositories.FrameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FrameUseCase @Inject constructor(
    private val repository: FrameRepository
) : BaseUseCase() {

    suspend fun getAllFrameCategories(isFetch: Boolean): Flow<DataState<List<FrameCategoryModel>>> {
        return repository.getAllFrameCategory(isFetch)
    }

    suspend fun getFrameByCategoryId(
        isFetch: Boolean,
        categoryId: Long
    ): Flow<DataState<List<FrameModel>>> {
        return repository.getFrameByCategoryId(isFetch, categoryId)
    }

    fun downLoadFrame(frameModel: FrameModel, onDone: (Boolean) -> Unit) =
        repository.downloadFrameZipRatio(frameModel, onDone)
}