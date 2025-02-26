package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.enums.CategoryState
import java.time.LocalDateTime

data class CopyCategoryResult(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val categoryState: CategoryState,
    val createdAt: LocalDateTime?
) {

    companion object {
        fun of(newCategory: Category): CopyCategoryResult {
            return CopyCategoryResult(
                categoryId = newCategory.id,
                categoryName = newCategory.categoryName,
                asCategoryName = newCategory.asCategoryName,
                categoryState = newCategory.categoryState,
                createdAt = newCategory.createdAt
            )
        }
    }

}