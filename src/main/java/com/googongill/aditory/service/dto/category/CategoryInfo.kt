package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.enums.CategoryState
import java.time.LocalDateTime

data class CategoryInfo(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val linkCount: Int,
    val likeCount: Int,
    val categoryState: CategoryState,
    val prevLinks: List<String> = emptyList(),
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
)
