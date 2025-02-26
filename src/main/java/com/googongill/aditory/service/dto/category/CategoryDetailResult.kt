package com.googongill.aditory.service.dto.category

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.service.dto.link.LinkInfo
import java.time.LocalDateTime

data class CategoryDetailResult(
    val categoryId: Long?,
    val categoryName: String,
    val asCategoryName: String,
    val linkCount: Int?,
    val likeCount: Int?,
    val categoryState: CategoryState,
    val linkList: List<LinkInfo> = emptyList(),
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
) {

    companion object {
        fun of(category: Category, linkList: List<LinkInfo>): CategoryDetailResult {
            return CategoryDetailResult(
                categoryId = category.id,
                categoryName = category.categoryName,
                asCategoryName = category.asCategoryName,
                linkCount = category.links.size,
                likeCount = category.categoryLikes.size,
                categoryState = category.categoryState,
                linkList = linkList,
                createdAt = category.createdAt,
                lastModifiedAt = category.lastModifiedAt
            )
        }
    }

}
