package com.googongill.aditory.controller.dto.user.response

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.domain.User
import com.googongill.aditory.external.s3.dto.S3DownloadResult
import com.googongill.aditory.service.dto.user.ProfileImageResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ProfileImageResponse(
    val userId: Long?,
    val username: String,
    val nickname: String?,
    val s3DownloadResult: S3DownloadResult?
) {

    companion object {
        fun of(profileImageResult: ProfileImageResult): ProfileImageResponse {
            return ProfileImageResponse(
                userId = profileImageResult.userId,
                username = profileImageResult.username,
                nickname = profileImageResult.nickname,
                s3DownloadResult = profileImageResult.s3DownloadResult
            )
        }

        fun of(user: User, s3DownloadResult: S3DownloadResult): ProfileImageResponse {
            return ProfileImageResponse(
                userId = user.id,
                username = user.username,
                nickname = user.nickname,
                s3DownloadResult = s3DownloadResult
            )
        }
    }

}
