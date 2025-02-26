package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.category.CopyCategoryResult
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CopyCategoryResponse(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val categoryState: CategoryState?,
    val createdAt: LocalDateTime?
) {

    companion object {
        fun of(copyCategoryResult: CopyCategoryResult): CopyCategoryResponse {
            return CopyCategoryResponse(
                categoryId = copyCategoryResult.categoryId,
                categoryName = copyCategoryResult.categoryName,
                asCategoryName = copyCategoryResult.asCategoryName,
                categoryState = copyCategoryResult.categoryState,
                createdAt = copyCategoryResult.createdAt
            )
        }
    }

}
