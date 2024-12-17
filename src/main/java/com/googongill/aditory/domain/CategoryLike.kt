package com.googongill.aditory.domain

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CategoryLike(@field:JoinColumn(name = "user_id", nullable = false) @field:ManyToOne(fetch = FetchType.LAZY) private val user: User, @field:JoinColumn(name = "category_id", nullable = false) @field:ManyToOne(fetch = FetchType.LAZY) private var category: Category) {
    @Id
    @Column(name = "category_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    // 연관관계 메서드
    fun setCategory(category: Category) {
        this.category = category
    }
}
