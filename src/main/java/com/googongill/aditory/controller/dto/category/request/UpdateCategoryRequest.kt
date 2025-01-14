package com.googongill.aditory.controller.dto.category.request

import com.googongill.aditory.domain.enums.CategoryState
import jakarta.validation.constraints.NotNull

class UpdateCategoryRequest(
    val categoryName: @NotNull String,
    val categoryState: CategoryState,
    val asCategoryName: String
)