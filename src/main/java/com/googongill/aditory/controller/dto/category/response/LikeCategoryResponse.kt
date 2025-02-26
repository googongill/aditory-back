package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.category.LikeCategoryResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LikeCategoryResponse(
    val categoryId: Long?,
    val likeCount: Int?
) {

    companion object {
        fun of(likeCategoryResult: LikeCategoryResult): LikeCategoryResponse {
            return LikeCategoryResponse(likeCategoryResult.categoryId, likeCategoryResult.likeCount)
        }
    }
}
