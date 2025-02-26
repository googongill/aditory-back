package com.googongill.aditory.service.dto.user

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User
import com.googongill.aditory.service.dto.category.CategoryResult
import java.util.stream.Collectors

data class SignupResult(
    val userId: Long?,
    val nickname: String?,
    val userCategories: List<CategoryResult> = emptyList()
) {

    companion object {
        fun of(user: User, categories: List<Category>): SignupResult {
            val userCategories = categories.stream()
                .map { category: Category ->
                    CategoryResult(
                        categoryId = category.id,
                        categoryName = category.categoryName
                    )
                }
                .collect(Collectors.toList())
            return SignupResult(
                userId = user.id,
                nickname = user.nickname,
                userCategories = userCategories
            )
        }
    }

}