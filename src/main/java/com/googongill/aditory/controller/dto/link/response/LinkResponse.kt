package com.googongill.aditory.controller.dto.link.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.link.LinkResult
import java.time.LocalDateTime

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LinkResponse(
    val linkId: Long?,
    val categoryId: Long?,
    val createdAt: LocalDateTime?,
    val lastModifiedAt: LocalDateTime?
) {

    companion object {
        fun of(linkResult: LinkResult): LinkResponse {
            return LinkResponse(
                linkId = linkResult.linkId,
                categoryId = linkResult.categoryId,
                createdAt = linkResult.createdAt,
                lastModifiedAt = linkResult.lastModifiedAt
            )
        }
    }

}
