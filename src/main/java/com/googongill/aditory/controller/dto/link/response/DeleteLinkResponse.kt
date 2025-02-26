package com.googongill.aditory.controller.dto.link.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class DeleteLinkResponse(
    val linkId: Long?
) {

    companion object {
        fun of(linkId: Long?): DeleteLinkResponse {
            return DeleteLinkResponse(
                linkId = linkId
            )
        }
    }
}
