package com.volio.vn.usecases

import com.volio.vn.models.transition.TransitionCategoryModel
import com.volio.vn.models.transition.TransitionModel
import com.volio.vn.repositories.DataState
import com.volio.vn.repositories.TransitionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransitionUseCase @Inject constructor(
    private val repo: TransitionRepository
) : BaseUseCase() {

    suspend fun addLocalTransition(transition: List<TransitionModel>): Flow<DataState<List<TransitionModel>>> {
        return repo.addLocalTransition(transition)
    }

    suspend fun getAllTransition(isFetch: Boolean): Flow<DataState<List<TransitionModel>>> {
        return repo.getAllTransition(isFetch)
    }

    suspend fun updateTransition(transition: TransitionModel): Flow<DataState<TransitionModel>> {
        return repo.updateTransition(transition)
    }

    suspend fun getTransitionCategory(isFetch: Boolean): Flow<DataState<List<TransitionCategoryModel>>> {
        return repo.getAllTransitionCategory(isFetch)
    }

    suspend fun getTransitionByIdCategory(
        isFetch: Boolean,
        idCategory: Long
    ): Flow<DataState<List<TransitionModel>>> {
        return repo.getTransitionByIdCategory(isFetch, idCategory)
    }

}
