package com.googongill.aditory.controller.dto.category.request

import jakarta.validation.constraints.NotBlank

data class MoveCategoryRequest(
    @NotBlank
    val linkIdList: List<Long>,
    @NotBlank
    val targetCategoryId: Long
)