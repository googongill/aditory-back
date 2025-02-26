package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.category.CategoryDetailResult
import com.googongill.aditory.service.dto.link.LinkInfo

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CategoryDetailResponse(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val linkCount: Int?,
    val likeCount: Int?,
    val categoryState: CategoryState,
    val linkList: List<LinkInfo> = emptyList()
) {

    companion object {
        fun of(categoryDetailResult: CategoryDetailResult): CategoryDetailResponse {
            return CategoryDetailResponse(
                categoryId = categoryDetailResult.categoryId,
                categoryName = categoryDetailResult.categoryName,
                asCategoryName = categoryDetailResult.asCategoryName,
                linkCount = categoryDetailResult.linkCount,
                likeCount = categoryDetailResult.likeCount,
                categoryState = categoryDetailResult.categoryState,
                linkList = categoryDetailResult.linkList
            )
        }
    }

}
