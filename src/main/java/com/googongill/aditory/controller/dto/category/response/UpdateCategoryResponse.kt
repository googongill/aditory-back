package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.category.UpdateCategoryResult
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UpdateCategoryResponse(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val categoryState: CategoryState,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?,
) {

    companion object {
        fun of(updateCategoryResult: UpdateCategoryResult): UpdateCategoryResponse {
            return UpdateCategoryResponse(
                categoryId = updateCategoryResult.categoryId,
                categoryName = updateCategoryResult.categoryName,
                asCategoryName = updateCategoryResult.asCategoryName,
                categoryState = updateCategoryResult.categoryState,
                createdAt = updateCategoryResult.createdAt,
                lastModifiedAt = updateCategoryResult.lastModifiedAt
            )
        }
    }
}
