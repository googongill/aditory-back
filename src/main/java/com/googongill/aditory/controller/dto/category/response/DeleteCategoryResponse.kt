package com.googongill.aditory.controller.dto.category.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class DeleteCategoryResponse(
    val categoryId: Long?
) {

    companion object {
        fun of(deletedCategoryId: Long?): DeleteCategoryResponse {
            return DeleteCategoryResponse(
                categoryId = deletedCategoryId
            )
        }
    }
}
