package com.volio.vn.usecases

import com.volio.vn.models.FolderModel
import com.volio.vn.models.MediaData
import com.volio.vn.models.VideoModel
import com.volio.vn.repositories.ReadFileLocalRepository
import javax.inject.Inject

class VideoUseCase @Inject constructor(
    private val readFileLocalRepo: ReadFileLocalRepository
) : BaseUseCase() {
    suspend fun getVideoLocal(): Result<Pair<List<MediaData>, List<FolderModel>>> {
        return runWithCheckExceptionByResult {
            readFileLocalRepo.getVideoLocal()
        }
    }
}