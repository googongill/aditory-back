package com.googongill.aditory.controller.dto.link.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.link.LinkInfo
import com.googongill.aditory.service.dto.link.LinkListResult
import org.springframework.data.domain.Page

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LinkListResponse(
    val linkList: List<LinkInfo> = emptyList(),
    val currentPage: Int?,
    val totalPages: Int?,
    val totalItems: Long?,
) {

    companion object {
        fun of(linkListResult: LinkListResult): LinkListResponse {
            return LinkListResponse(
                linkList = linkListResult.linkList,
                currentPage = 0,
                totalPages = 1,
                totalItems = java.lang.Long.valueOf(linkListResult.linkList.size.toLong())
            )
        }

        fun of(linkInfoPage: Page<LinkInfo>): LinkListResponse {
            return LinkListResponse(
                linkList = linkInfoPage.content,
                currentPage = linkInfoPage.number,
                totalPages = linkInfoPage.totalPages,
                totalItems = linkInfoPage.totalElements
            )
        }
    }
}
