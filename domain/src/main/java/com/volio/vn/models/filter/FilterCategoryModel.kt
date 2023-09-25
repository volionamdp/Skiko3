package com.volio.vn.models.filter

import com.volio.vn.models.category.CategoryModel

data class FilterCategoryModel(
    override val id: Long,
    override val name: String
) : CategoryModel
