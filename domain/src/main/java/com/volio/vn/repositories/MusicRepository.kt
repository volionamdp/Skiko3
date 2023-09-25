package com.volio.vn.repositories

import com.volio.vn.models.music.MusicCategoryModel
import com.volio.vn.models.music.MusicModel
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getAllMusic(isFetch: Boolean): List<MusicModel>

    suspend fun getAllCategoryMusic(isFetch: Boolean): List<MusicCategoryModel>

    suspend fun getMusicByIdCategory(
        isFetch: Boolean,
        idCategory: Long
    ): List<MusicModel>

    suspend fun updateMusic(transition: MusicModel): MusicModel?

}
