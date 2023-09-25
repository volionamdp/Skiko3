package com.volio.vn.models.theme

import com.volio.vn.models.category.CategoryModel

data class ThemeCategoryModel(override val id: Long, override val name: String) : CategoryModel
