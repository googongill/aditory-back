package com.googongill.aditory.domain

import jakarta.persistence.*

@Entity
class ProfileImage(
        var originalName: String,
        var uploadedName: String
) : BaseTimeEntity() {
    @Id
    @Column(name = "profile_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateProfileImage(profileImage: ProfileImage) {
        originalName = profileImage.originalName
        uploadedName = profileImage.uploadedName
    }
}