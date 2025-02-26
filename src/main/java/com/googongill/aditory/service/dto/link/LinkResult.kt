package com.googongill.aditory.service.dto.link

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.Link
import java.time.LocalDateTime

data class LinkResult(
    val linkId: Long?,
    val categoryId: Long?,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
) {

    companion object {
        fun of(link: Link, category: Category): LinkResult {
            return LinkResult(
                linkId = link.id,
                categoryId = category.id,
                createdAt = link.createdAt,
                lastModifiedAt = link.lastModifiedAt
            )
        }
    }
}
