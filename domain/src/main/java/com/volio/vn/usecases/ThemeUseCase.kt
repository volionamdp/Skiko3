package com.volio.vn.usecases

import com.volio.vn.models.theme.ThemeCategoryModel
import com.volio.vn.models.theme.ThemeModel
import com.volio.vn.repositories.DataState
import com.volio.vn.repositories.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) : BaseUseCase() {
    suspend fun getAllThemeCategories(isFetch: Boolean): Flow<DataState<List<ThemeCategoryModel>>> =
        themeRepository.getAllThemeCategory(isFetch)

    suspend fun getThemeByCategoryId(
        isFetch: Boolean,
        categoryId: Long
    ): Flow<DataState<List<ThemeModel>>> = themeRepository.getThemeByCategoryId(isFetch, categoryId)

    fun downLoadTheme(themeModel: ThemeModel, onDone: (Boolean) -> Unit) =
        themeRepository.downloadThemeZip(themeModel, onDone)
}