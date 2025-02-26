package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.Category

data class LikeCategoryResult(
    val categoryId: Long?,
    val likeCount: Int?
) {

    companion object {
        fun of(category: Category): LikeCategoryResult {
            return LikeCategoryResult(
                categoryId = category.id,
                likeCount = category.categoryLikes.size
            )
        }
    }

}
