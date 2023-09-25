package com.volio.vn.usecases

import com.volio.vn.models.transition.TransitionModel
import com.volio.vn.repositories.DataState
import com.volio.vn.repositories.SlideShowRepository
import com.volio.vn.repositories.TransitionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SlideShowUseCase @Inject constructor(
    private val repo: TransitionRepository
) : BaseUseCase() {

    suspend fun getTransition(): Flow<DataState<List<TransitionModel>>> {
        return repo.getAllTransition(true)
    }


}


