package com.volio.vn.repositories

import com.volio.vn.models.theme.ThemeCategoryModel
import com.volio.vn.models.theme.ThemeModel
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun getAllThemeCategory(isFetch: Boolean): Flow<DataState<List<ThemeCategoryModel>>>
    suspend fun getThemeByCategoryId(
        isFetch: Boolean,
        categoryId: Long
    ): Flow<DataState<List<ThemeModel>>>

    fun downloadThemeZip(themeModel: ThemeModel,onDone:(Boolean)->Unit)
}