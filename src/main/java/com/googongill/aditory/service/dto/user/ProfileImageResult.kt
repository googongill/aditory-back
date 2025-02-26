package com.googongill.aditory.service.dto.user

import com.googongill.aditory.domain.User
import com.googongill.aditory.external.s3.dto.S3DownloadResult

data class ProfileImageResult(
    val userId: Long?,
    val username: String,
    val nickname: String?,
    val s3DownloadResult: S3DownloadResult?
) {

    companion object {
        @JvmStatic
        fun of(user: User, s3DownloadResult: S3DownloadResult?): ProfileImageResult {
            return ProfileImageResult(
                userId = user.id,
                username = user.username,
                nickname = user.nickname,
                s3DownloadResult = s3DownloadResult
            )
        }
    }

}
