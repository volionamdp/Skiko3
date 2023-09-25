package com.volio.vn.models.frame

import com.volio.vn.models.category.CategoryModel

data class FrameCategoryModel(override val id: Long, override val name: String) : CategoryModel
