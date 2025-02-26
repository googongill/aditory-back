package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.user.UpdateUserResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UpdateUserResponse(
    val userId: Long?,
    val nickname: String?,
    val contact: String?
) {

    companion object {
        fun of(updateUserResult: UpdateUserResult): UpdateUserResponse {
            return UpdateUserResponse(
                userId = updateUserResult.userId,
                nickname = updateUserResult.nickname,
                contact = updateUserResult.contact
            )
        }
    }

}
