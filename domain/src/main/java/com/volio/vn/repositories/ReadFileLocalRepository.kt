package com.volio.vn.repositories

import android.net.Uri
import com.volio.vn.models.FolderModel
import com.volio.vn.models.MediaData

interface ReadFileLocalRepository {

    suspend fun getMusicLocal(): List<MediaData>

    suspend fun scanUriThenGetMusicLocal(uri : Uri): List<MediaData>

    suspend fun getPhotoLocal(): Pair<List<MediaData>, List<FolderModel>>

    suspend fun getVideoMyCreated(): List<MediaData>

    suspend fun getVideoLocal(): Pair<List<MediaData>, List<FolderModel>>
}