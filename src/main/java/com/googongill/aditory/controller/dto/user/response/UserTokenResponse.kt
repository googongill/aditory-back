package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.category.CategoryResult
import com.googongill.aditory.service.dto.user.UserTokenResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UserTokenResponse(
    val userId: Long?,
    val username: String,
    val nickname: String?,
    val contact: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val userCategories: List<CategoryResult> = emptyList()

) {

    companion object {
        fun of(userTokenResult: UserTokenResult): UserTokenResponse {
            return UserTokenResponse(
                userId = userTokenResult.userId,
                username = userTokenResult.username,
                nickname = userTokenResult.nickname,
                contact = userTokenResult.contact,
                accessToken = userTokenResult.accessToken,
                refreshToken = userTokenResult.refreshToken,
                userCategories = userTokenResult.userCategories
            )
        }
    }

}
