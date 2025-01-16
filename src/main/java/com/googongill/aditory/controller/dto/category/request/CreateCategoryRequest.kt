package com.googongill.aditory.controller.dto.category.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User
import jakarta.validation.constraints.NotBlank


data class CreateCategoryRequest(
    @NotBlank
    @JsonProperty("categoryName")
    val categoryName: String
) {
    fun toEntity(user: User?): Category {
        return Category(categoryName, categoryName, user!!)
    }
}
