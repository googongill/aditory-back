package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.category.CategoryInfo
import com.googongill.aditory.service.dto.category.CategoryListResult
import org.springframework.data.domain.Page

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CategoryListResponse(
    var categoryList: List<CategoryInfo> = emptyList(),
    val currentPage: Int?,
    val totalPages: Int?,
    val totalItems: Long?
) {

    companion object {
        fun of(categoryListResult: CategoryListResult): CategoryListResponse {
            return CategoryListResponse(
                categoryList = categoryListResult.categoryList,
                currentPage = 0,
                totalPages = 1,
                totalItems = java.lang.Long.valueOf(categoryListResult.categoryList.size.toLong())
            )
        }

        fun of(categoryInfoPage: Page<CategoryInfo>): CategoryListResponse {
            return CategoryListResponse(
                categoryList = categoryInfoPage.content,
                currentPage = categoryInfoPage.number,
                totalPages = categoryInfoPage.totalPages,
                totalItems = categoryInfoPage.totalElements
            )
        }
    }
}
