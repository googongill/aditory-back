package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.category.LikeCategoryListResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LikeCategoryListResponse(
    var likeCategoryList: List<Long> = emptyList()
) {

    companion object {
        fun of(likeCategoryListResult: LikeCategoryListResult): LikeCategoryListResponse {
            return LikeCategoryListResponse(
                likeCategoryList = likeCategoryListResult.likeCategoryList
            )
        }
    }
}
