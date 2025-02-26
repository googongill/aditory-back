package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.service.dto.category.CategoryResult
import com.googongill.aditory.service.dto.user.SignupResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SignupResponse(
    val userId: Long?,
    val nickname: String?,
    val userCategories: List<CategoryResult> = emptyList()
) {

    companion object {
        fun of(signupResult: SignupResult): SignupResponse {
            return SignupResponse(
                userId = signupResult.userId,
                nickname = signupResult.nickname,
                userCategories = signupResult.userCategories
            )
        }
    }
}
