package com.googongill.aditory.controller.dto.link.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.Link
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LinkDetailResponse(
    val linkId: Long?,
    val title: String,
    val summary: String?,
    val url: String,
    val linkState: Boolean,
    val categoryId: Long?,
    val categoryName: String,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
) {

    companion object {
        fun of(link: Link): LinkDetailResponse {
            return LinkDetailResponse(
                linkId = link.id,
                title = link.title,
                summary = link.summary,
                linkState = link.linkState,
                url = link.url,
                categoryId = link.category.id,
                categoryName = link.category.categoryName,
                createdAt = link.createdAt,
                lastModifiedAt = link.lastModifiedAt
            )
        }
    }

}
