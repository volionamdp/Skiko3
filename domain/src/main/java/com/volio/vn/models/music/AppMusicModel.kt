package com.volio.vn.models.music

data class AppMusicModel(
    val musicCategory: MusicCategoryModel,
    val musics: MutableList<MusicModel>
)