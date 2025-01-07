package com.googongill.aditory.domain

import com.googongill.aditory.domain.enums.Role
import com.googongill.aditory.domain.enums.SocialType
import jakarta.persistence.*
import java.util.*

@Entity
class User : BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var username: String
    lateinit var password: String

    @Enumerated(EnumType.STRING)
    var role: Role

    @Enumerated(EnumType.STRING)
    var socialType: SocialType

    var socialId: String? = null
    var nickname: String? = null
    var contact: String? = null
    var refreshToken: String? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableList<Category> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var links: MutableList<Link> = mutableListOf()

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    var profileImage: ProfileImage? = null

    constructor(username: String, password: String, role: Role, socialType: SocialType, nickname: String, contact: String?) {
        this.username = username
        this.password = password
        this.role = role
        this.socialType = socialType
        this.nickname = nickname
        this.contact = contact
    }

    constructor(username: String, role: Role, socialType: SocialType, socialId: String, nickname: String) {
        this.username = username
        this.role = role
        this.socialType = socialType
        this.socialId = socialId
        this.nickname = nickname
    }

    // 연관관계 메서드
    fun addCategory(category: Category) {
        categories.add(category)
        category.createUser(this)
    }

    fun addCategories(categories: List<Category>) {
        for (category in categories) {
            addCategory(category)
        }
    }

    fun addLink(link: Link) {
        links.add(link)
        link.updateUser(this)
    }

    fun updateProfileImage(profileImage: ProfileImage?) {
        this.profileImage = profileImage
    }

    fun saveRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }

    fun deleteRefreshToken() {
        refreshToken = null
    }

    fun updateUserInfo(nickname: String, contact: String?) {
        this.nickname = nickname
        this.contact = contact
    }

    fun fetchProfileImage(): Optional<ProfileImage> {
        return Optional.ofNullable(profileImage)
    }
}