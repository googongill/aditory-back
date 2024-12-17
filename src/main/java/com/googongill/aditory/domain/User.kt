package com.googongill.aditory.domain

import com.googongill.aditory.domain.enums.Role
import com.googongill.aditory.domain.enums.SocialType
import jakarta.persistence.*
//import lombok.AccessLevel
//import lombok.Getter
//import lombok.NoArgsConstructor
import java.util.*

@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
class User : BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
    private var username: String
    private var password: String? = null

    @Enumerated(EnumType.STRING)
    private var role: Role

    @Enumerated(EnumType.STRING)
    private var socialType: SocialType
    private var socialId: String? = null
    private var nickname: String
    private var contact: String? = null
    private var refreshToken: String? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val categories: MutableList<Category> = ArrayList()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val links: MutableList<Link> = ArrayList()

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    private var profileImage: ProfileImage? = null

    constructor(username: String, password: String?, role: Role, socialType: SocialType, nickname: String, contact: String?) {
        this.username = username
        this.password = password
        this.role = role
        this.socialType = socialType
        this.nickname = nickname
        this.contact = contact
    }

    constructor(username: String, role: Role, socialType: SocialType, socialId: String?, nickname: String) {
        this.username = username
        this.role = role
        this.socialType = socialType
        this.socialId = socialId
        this.nickname = nickname
    }

    // getter
    fun getId(): Long? = id
    fun getUsername(): String? = username
    fun getPassword(): String? = password
    fun getRole(): Role = role
    fun getSocialType(): SocialType = socialType
    fun getSocialId(): String? = socialId
    fun getNickname(): String? = nickname
    fun getContact(): String? = contact
    fun getRefreshToken(): String? = refreshToken
    fun getCategories(): MutableList<Category> = categories
    fun getLinks(): MutableList<Link> = links

    // 연관관계 메서드
    fun addCategory(category: Category) {
        categories.add(category)
        category.setUser(this)
    }

    fun addCategories(categories: List<Category>) {
        for (category in categories) {
            this.categories.add(category)
            category.setUser(this)
        }
    }

    fun addLink(link: Link) {
        links.add(link)
        link.setUser(this)
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

    fun getProfileImage(): Optional<ProfileImage> {
        return Optional.ofNullable(profileImage)
    }
}
