package com.googongill.aditory.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeEntity(
        @CreatedDate
        @Column(updatable = false)
        var createdAt: LocalDateTime? = null,

        @LastModifiedDate
        var lastModifiedAt: LocalDateTime? = null
)