package com.googongill.aditory.domain

import jakarta.persistence.*

@Entity
class CategoryLike(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        var user: User,
        @JoinColumn(name = "category_id", nullable = false)
        @ManyToOne(fetch = FetchType.LAZY)
        var category: Category
) {
    @Id
    @Column(name = "category_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    // 연관관계 메서드
    fun updateCategory(category: Category) {
        this.category = category
    }
}
