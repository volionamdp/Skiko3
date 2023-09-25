package com.volio.vn.models.transition

import com.volio.vn.models.category.CategoryModel

data class TransitionCategoryModel(override val id: Long, override val name: String) :
    CategoryModel {
    companion object {
        const val CATEGORY_GLSL = -1L
    }
}
