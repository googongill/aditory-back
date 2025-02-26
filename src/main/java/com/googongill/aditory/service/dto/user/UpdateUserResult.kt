package com.googongill.aditory.service.dto.user

import com.googongill.aditory.domain.User

data class UpdateUserResult(
    val userId: Long?,
    val nickname: String?,
    val contact: String?
) {

    companion object {
        fun of(user: User): UpdateUserResult {
            return UpdateUserResult(
                userId = user.id,
                nickname = user.nickname,
                contact = user.contact
            )
        }
    }
}
