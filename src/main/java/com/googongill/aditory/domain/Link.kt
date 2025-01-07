package com.googongill.aditory.domain

import jakarta.persistence.*

@Entity
class Link : BaseEntity {
    @Id
    @Column(name = "link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var title: String
    var summary: String? = null
    var url: String
    var linkState: Boolean

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User

    constructor(title: String, summary: String?, url: String, category: Category, user: User) {
        this.title = title
        this.summary = summary
        this.url = url
        linkState = false
        this.category = category
        this.user = user
    }

    constructor(title: String, url: String, linkState: Boolean, category: Category, user: User) {
        this.title = title
        this.url = url
        this.linkState = linkState
        this.category = category
        this.user = user
    }

    // 연관관계 메서드
    fun updateCategory(category: Category) {
        this.category = category
    }

    fun updateLinkInfo(title: String, summary: String?, url: String, category: Category) {
        this.title = title
        this.summary = summary
        this.url = url
        this.category = category
    }

    fun updateUser(user: User) {
        this.user = user
    }

    fun updateLinkState() {
        linkState = true
    }
}
