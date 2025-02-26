package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.category.CreateCategoryResult
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CreateCategoryResponse(
    val categoryId: Long?,
    val categoryName: String,
    val categoryState: CategoryState,
    val createdAt: LocalDateTime?
) {

    companion object {
        fun of(createCategoryResult: CreateCategoryResult): CreateCategoryResponse {
            return CreateCategoryResponse(
                categoryId = createCategoryResult.categoryId,
                categoryName = createCategoryResult.categoryName,
                categoryState = createCategoryResult.categoryState,
                createdAt = createCategoryResult.createdAt
            )
        }
    }

}