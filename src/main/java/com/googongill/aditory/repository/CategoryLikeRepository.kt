package com.googongill.aditory.repository

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.CategoryLike
import com.googongill.aditory.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryLikeRepository : JpaRepository<CategoryLike?, Long?> {

    fun existsByUserAndCategory(user: User, category: Category): Boolean

    fun findByUserAndCategory(user: User, category: Category): CategoryLike?

    fun findByUser(user: User): List<CategoryLike>

}