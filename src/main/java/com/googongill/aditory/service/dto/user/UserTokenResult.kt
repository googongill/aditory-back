package com.googongill.aditory.service.dto.user

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User
import com.googongill.aditory.security.jwt.dto.JwtResult
import com.googongill.aditory.service.dto.category.CategoryResult
import java.util.stream.Collectors

data class UserTokenResult(
    val userId: Long?,
    val username: String,
    val nickname: String?,
    val contact: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val userCategories: List<CategoryResult> = emptyList()
) {

    companion object {
        fun of(user: User, jwtResult: JwtResult): UserTokenResult {
            val userCategories = user.categories.stream()
                .map { category: Category ->
                    CategoryResult(
                        categoryId = category.id,
                        categoryName = category.categoryName
                    )
                }
                .collect(Collectors.toList())
            return UserTokenResult(
                userId = user.id,
                username = user.username,
                nickname = user.nickname,
                contact = user.contact,
                accessToken = jwtResult.accessToken,
                refreshToken = jwtResult.refreshToken,
                userCategories = userCategories
            )
        }
    }

}
