package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SignoutResponse(
    val userId: Long?,
    val username: String
) {

    companion object {
        fun of(userId: Long?, username: String): SignoutResponse {
            return SignoutResponse(
                userId = userId,
                username = username
            )
        }
    }

}
