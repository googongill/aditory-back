package com.googongill.aditory.repository

import com.googongill.aditory.domain.Link
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.CategoryState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LinkRepository : JpaRepository<Link?, Long?> {

    fun findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user: User, linkState: Boolean): List<Link>

    fun findByTitleContaining(title: String): List<Link>

    fun findByTitleContainingAndUser(query: String, user: User, pageable: Pageable): Page<Link>

    fun findByTitleContainingAndCategory_CategoryState(query: String, categoryState: CategoryState, pageable: Pageable): Page<Link>

}
