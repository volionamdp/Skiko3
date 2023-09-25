package com.volio.vn.repositories

import com.volio.vn.models.music.MusicCategoryModel
import com.volio.vn.models.music.MusicModel
import kotlinx.coroutines.flow.Flow

interface SlideShowRepository {

    suspend fun getAllMusic(isFetch: Boolean): Flow<DataState<List<MusicModel>>>
    suspend fun getAllCategoryMusic(isFetch: Boolean): Flow<DataState<List<MusicCategoryModel>>>
    suspend fun getMusicByIdCategory(
        isFetch: Boolean,
        idCategory: Long
    ): Flow<DataState<List<MusicModel>>>

}