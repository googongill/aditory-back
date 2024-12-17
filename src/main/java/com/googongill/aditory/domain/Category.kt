package com.googongill.aditory.domain

import com.googongill.aditory.domain.enums.CategoryState
import jakarta.persistence.*
import java.time.LocalDateTime

//import lombok.AccessLevel
//import lombok.Getter
//import lombok.NoArgsConstructor

@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Category : BaseEntity {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
    private var categoryName: String
    private var asCategoryName: String? = null

    @Enumerated(EnumType.STRING)
    private var categoryState: CategoryState? = null
    private var viewCount: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private var user: User? = null

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val links: MutableList<Link> = ArrayList()

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private val categoryLikes: MutableList<CategoryLike> = ArrayList()

    constructor(categoryName: String) {
        this.categoryName = categoryName
    }

    constructor(categoryName: String, asCategoryName: String?, user: User?) {
        this.categoryName = categoryName
        this.asCategoryName = asCategoryName
        viewCount = 0
        categoryState = CategoryState.PRIVATE
        this.user = user
    }

    // getter
    fun getId(): Long? = id
    fun getCategoryName(): String = categoryName
    fun getAsCategoryName(): String? = asCategoryName
    fun getCategoryState(): CategoryState? = categoryState
    fun getUser(): User? = user
    fun getLinks(): MutableList<Link> = links
    fun getCategoryLikes(): MutableList<CategoryLike> = categoryLikes

    // 연관관계 메서드
    fun setUser(user: User?) {
        this.user = user
    }

    fun addLink(link: Link) {
        links.add(link)
        link.setCategory(this)
    }

    fun addCategoryLike(categoryLike: CategoryLike) {
        categoryLikes.add(categoryLike)
        categoryLike.setCategory(this)
    }

    fun deleteCategoryLike(categoryLike: CategoryLike) {
        categoryLikes.remove(categoryLike)
    }

    fun updateCategoryInfo(categoryName: String, asCategoryName: String?, categoryState: CategoryState?) {
        this.categoryName = categoryName
        this.asCategoryName = asCategoryName
        this.categoryState = categoryState
    }
}
