package com.volio.vn.repositories

import com.volio.vn.models.transition.TransitionCategoryModel
import com.volio.vn.models.transition.TransitionModel
import kotlinx.coroutines.flow.Flow

interface TransitionRepository {
    suspend fun addLocalTransition(transition: List<TransitionModel>): Flow<DataState<List<TransitionModel>>>
    suspend fun updateTransition(transition: TransitionModel): Flow<DataState<TransitionModel>>
    suspend fun getAllTransition(isFetch: Boolean): Flow<DataState<List<TransitionModel>>>
    suspend fun getAllTransitionCategory(isFetch: Boolean): Flow<DataState<List<TransitionCategoryModel>>>
    suspend fun getTransitionByIdCategory(
        isFetch: Boolean,
        idCategory: Long
    ): Flow<DataState<List<TransitionModel>>>
}