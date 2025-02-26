package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.Category
import com.googongill.aditory.service.dto.category.CategoryResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ImportCategoryResponse(
    val userCategories: List<CategoryResult> = emptyList()
) {

    companion object {
        fun of(categories: List<Category>): ImportCategoryResponse {
            return ImportCategoryResponse(
                userCategories = categories.map {category ->
                    CategoryResult(
                        categoryId = category.id,
                        categoryName = category.categoryName
                    )
                }
            )
        }
    }

}
