package com.googongill.aditory.external.s3.dto

data class S3DownloadResult(
    val profileImageId: Long,
    val originalName: String,
    val url: String
) {
    companion object {
        fun of(profileImageId: Long, originalName: String, url: String): S3DownloadResult {
            return S3DownloadResult(
                profileImageId = profileImageId,
                originalName = originalName,
                url = url
            )
        }
    }

}
