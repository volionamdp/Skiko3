package com.volio.vn.usecases

import android.net.Uri
import com.volio.vn.models.music.AppMusicModel
import com.volio.vn.models.music.MusicCategoryModel
import com.volio.vn.models.music.MusicModel
import com.volio.vn.repositories.MusicRepository
import com.volio.vn.repositories.ReadFileLocalRepository
import javax.inject.Inject

class MusicUseCase @Inject constructor(
    private val repo: MusicRepository,
    private val readFileLocalRepo: ReadFileLocalRepository
) : BaseUseCase() {

    suspend fun getLocalMusic(): Result<List<MusicModel>> {
        return runWithCheckExceptionByResult {
            readFileLocalRepo.getMusicLocal().map {
                it as MusicModel
            }
        }
    }

    suspend fun scanUriThenGetLocalMusic(uri : Uri): Result<List<MusicModel>> {
        return runWithCheckExceptionByResult {
            readFileLocalRepo.scanUriThenGetMusicLocal(uri).map {
                it as MusicModel
            }
        }
    }

    suspend fun getAllMusic(isFetch: Boolean): List<MusicModel> {
        return repo.getAllMusic(isFetch)
    }

    suspend fun updateMusic(musicModel: MusicModel): MusicModel? {
        return repo.updateMusic(musicModel)
    }

    suspend fun getAllCategoryMusic(isFetch: Boolean): List<MusicCategoryModel> {
        return repo.getAllCategoryMusic(isFetch)
    }

    suspend fun getMusicByIdCategory(
        isFetch: Boolean,
        idCategory: Long
    ): List<MusicModel> {
        return repo.getMusicByIdCategory(isFetch, idCategory)
    }

    suspend fun getMusicWithCategory(): List<AppMusicModel> {
        val appMusic = mutableListOf<AppMusicModel>()
        getAllCategoryMusic(true).forEach {
            val musics = getMusicByIdCategory(true, it.id)
            val appMusicModel = AppMusicModel(it, musics.toMutableList())
            appMusic.add(appMusicModel)
        }
        return appMusic
    }

}
