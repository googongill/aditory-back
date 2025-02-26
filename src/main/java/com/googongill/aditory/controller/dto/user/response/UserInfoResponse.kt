package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UserInfoResponse(
    val userId: Long?,
    val username: String,
    val nickname: String?,
    val aditoryPower: Int?
) {

    companion object {
        fun of(user: User): UserInfoResponse {
            val totalLikes = user.categories.stream()
                .mapToInt { category: Category -> category.categoryLikes.size }
                .sum()
            val aditoryPower = user.categories.size + user.links.size + totalLikes
            return UserInfoResponse(
                userId = user.id,
                username = user.username,
                nickname = user.nickname,
                aditoryPower = aditoryPower
            )

        }
    }

}