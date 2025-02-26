package com.googongill.aditory.service.dto.link

data class LinkListResult(
    val linkList: List<LinkInfo> = emptyList()

) {

    companion object {
        fun of(linkInfoList: List<LinkInfo>): LinkListResult {
            return LinkListResult(
                linkList = linkInfoList
            )
        }
    }

}
