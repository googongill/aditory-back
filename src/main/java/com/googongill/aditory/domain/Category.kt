package com.googongill.aditory.domain

import com.googongill.aditory.domain.enums.CategoryState
import jakarta.persistence.*

@Entity
class Category : BaseEntity {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var categoryName: String
    lateinit var asCategoryName: String

    @Enumerated(EnumType.STRING)
    lateinit var categoryState: CategoryState

    var viewCount: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    var links: MutableList<Link> = ArrayList()

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    var categoryLikes: MutableList<CategoryLike> = ArrayList()

    constructor(categoryName: String) {
        this.categoryName = categoryName
    }

    constructor(categoryName: String, asCategoryName: String, user: User) {
        this.categoryName = categoryName
        this.asCategoryName = asCategoryName
        viewCount = 0
        categoryState = CategoryState.PRIVATE
        this.user = user
    }

    // 연관관계 메서드
    fun createUser(user: User) {
        this.user = user
    }

    fun addLink(link: Link) {
        links.add(link)
        link.updateCategory(this)
    }

    fun addCategoryLike(categoryLike: CategoryLike) {
        categoryLikes.add(categoryLike)
        categoryLike.updateCategory(this)
    }

    fun deleteCategoryLike(categoryLike: CategoryLike) {
        categoryLikes.remove(categoryLike)
    }

    fun updateCategoryInfo(categoryName: String, asCategoryName: String, categoryState: CategoryState) {
        this.categoryName = categoryName
        this.asCategoryName = asCategoryName
        this.categoryState = categoryState
    }
}
