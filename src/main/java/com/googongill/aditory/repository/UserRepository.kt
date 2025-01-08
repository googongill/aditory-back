package com.googongill.aditory.repository

import com.googongill.aditory.domain.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User?, Long?> {
    @EntityGraph(attributePaths = ["categories", "categories.links", "links"])
    fun findById(id: Long): User?

    fun findByUsername(username: String?): User?

    fun findBySocialId(socialId: String?): User?
}
