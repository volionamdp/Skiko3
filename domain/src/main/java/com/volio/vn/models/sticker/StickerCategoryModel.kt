package com.volio.vn.models.sticker

import com.volio.vn.models.category.CategoryModel

data class StickerCategoryModel(override val id: Long, override val name: String) : CategoryModel
