package com.googongill.aditory.controller.dto.category.request

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User
import jakarta.validation.constraints.NotBlank


data class CreateCategoryRequest(
    @NotBlank
    val categoryName: String
) {
    fun toEntity(user: User?): Category {
        return Category(categoryName, categoryName, user!!)
    }
}
