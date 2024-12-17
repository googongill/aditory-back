package com.googongill.aditory.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
//import lombok.Getter
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

//@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity : BaseTimeEntity() {
    @CreatedBy
    @Column(updatable = false)
    private val createdBy: String? = null

    @LastModifiedBy
    private val lastModifiedBy: String? = null
}
