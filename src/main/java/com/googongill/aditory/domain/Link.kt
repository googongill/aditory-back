package com.googongill.aditory.domain

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Link : BaseEntity {
    @Id
    @Column(name = "link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
    private var title: String
    private var summary: String? = null
    private var url: String
    private var linkState: Boolean

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private var category: Category

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private var user: User

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
    fun setCategory(category: Category) {
        this.category = category
    }

    fun updateLinkInfo(title: String, summary: String?, url: String, category: Category) {
        this.title = title
        this.summary = summary
        this.url = url
        this.category = category
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun updateLinkState() {
        linkState = true
    }
}
