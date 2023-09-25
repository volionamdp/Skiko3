package com.volio.vn.usecases

import com.volio.vn.models.MediaData
import com.volio.vn.repositories.ReadFileLocalRepository
import javax.inject.Inject

class MyCreatedUseCase @Inject constructor(
    private val readFileLocalRepo: ReadFileLocalRepository
) : BaseUseCase() {

    suspend fun getMyCreated(): Result<List<MediaData>> {
        return runWithCheckExceptionByResult {
            readFileLocalRepo.getVideoMyCreated()
        }
    }

}
