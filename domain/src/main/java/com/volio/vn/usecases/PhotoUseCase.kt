package com.volio.vn.usecases

import com.volio.vn.models.FolderModel
import com.volio.vn.models.MediaData
import com.volio.vn.models.photo.PhotoModel
import com.volio.vn.repositories.ReadFileLocalRepository
import javax.inject.Inject

class PhotoUseCase @Inject constructor(
    private val readFileLocalRepo: ReadFileLocalRepository
) : BaseUseCase() {

    suspend fun getLocalPhoto(): Result<Pair<List<MediaData>, List<FolderModel>>> {
        return runWithCheckExceptionByResult {
            readFileLocalRepo.getPhotoLocal()
        }
    }

}
