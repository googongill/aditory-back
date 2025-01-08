package com.googongill.aditory.repository

import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.CategoryState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CategoryRepository : JpaRepository<Category?, Long?> {
    @EntityGraph(attributePaths = ["links"])
    fun findById(id: Long): Category?

    fun findByCategoryName(categoryName: String?): Category?

    @Query(value = "select c from Category c where c.categoryState = :categoryState")
    fun findAllByCategoryState(@Param("categoryState") categoryState: CategoryState, pageable: Pageable): Page<Category>

    @Query(
        value = "select * from category where category_state = :categoryState order by rand() limit 10",
        nativeQuery = true
    )
    fun findRandomByCategoryState(@Param("categoryState") categoryState: String): List<Category>

    fun findByAsCategoryNameContaining(categoryName: String): List<Category>

    fun findByCategoryNameAndUser(categoryName: String, user: User): Category?

    fun findByCategoryNameContainingAndUser(query: String, user: User, pageable: Pageable): Page<Category>

    fun findByAsCategoryNameContainingAndCategoryState(query: String, categoryState: CategoryState, pageable: Pageable): Page<Category>
}
