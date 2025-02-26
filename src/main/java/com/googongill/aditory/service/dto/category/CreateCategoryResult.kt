package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.enums.CategoryState
import java.time.LocalDateTime

data class CreateCategoryResult(
    val categoryId: Long?,
    val categoryName: String,
    val categoryState: CategoryState,
    val createdAt: LocalDateTime?,
) {

    companion object {
        fun of(category: Category): CreateCategoryResult {
            return CreateCategoryResult(
                categoryId = category.id,
                categoryName = category.categoryName,
                categoryState = category.categoryState,
                createdAt = category.createdAt
            )
        }
    }
}
