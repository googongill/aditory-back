package com.googongill.aditory.domain

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ProfileImage(private var originalName: String, private var uploadedName: String) : BaseTimeEntity() {
    @Id
    @Column(name = "profile_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
    fun updateProfileImage(profileImage: ProfileImage) {
        originalName = profileImage.getOriginalName()
        uploadedName = profileImage.getUploadedName()
    }
}
