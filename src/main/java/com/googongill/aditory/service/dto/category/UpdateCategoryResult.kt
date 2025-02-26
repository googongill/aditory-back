package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.enums.CategoryState
import java.time.LocalDateTime

data class UpdateCategoryResult(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val categoryState: CategoryState,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
) {

    companion object {
        fun of(category: Category): UpdateCategoryResult {
            return UpdateCategoryResult(
                categoryId = category.id,
                categoryName = category.categoryName,
                asCategoryName = category.asCategoryName,
                categoryState = category.categoryState,
                createdAt = category.createdAt,
                lastModifiedAt = category.lastModifiedAt
            )
        }
    }
}
