package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.category.CategoryDetailResult
import com.googongill.aditory.service.dto.link.LinkInfo
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class MoveCategoryResponse(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val linkCount: Int?,
    val categoryState: CategoryState,
    val linkList: List<LinkInfo> = emptyList(),
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?

) {

    companion object {
        fun of(categoryDetailResult: CategoryDetailResult): MoveCategoryResponse {
            return MoveCategoryResponse(
                categoryId = categoryDetailResult.categoryId,
                categoryName = categoryDetailResult.categoryName,
                asCategoryName = categoryDetailResult.asCategoryName,
                linkCount = categoryDetailResult.linkCount,
                categoryState = categoryDetailResult.categoryState,
                linkList = categoryDetailResult.linkList,
                createdAt = categoryDetailResult.createdAt,
                lastModifiedAt = categoryDetailResult.lastModifiedAt
            )
        }
    }

}