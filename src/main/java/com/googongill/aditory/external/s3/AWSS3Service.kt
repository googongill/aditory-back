package com.googongill.aditory.external.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.googongill.aditory.common.code.AWSS3ErrorCode
import com.googongill.aditory.common.code.AWSS3ErrorCode.*
import com.googongill.aditory.domain.ProfileImage
import com.googongill.aditory.exception.AWSS3Exception
import com.googongill.aditory.external.s3.dto.S3DownloadResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
class AWSS3Service(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.region.static}")
    private val area: String
) {

    fun uploadOne(multipartFile: MultipartFile): ProfileImage {
        return upload(multipartFile)
    }

    private fun upload(multipartFile: MultipartFile): ProfileImage {
        val ext = "." + multipartFile.originalFilename?.split("\\.(?=[^\\.]+$)".toRegex())?.get(1)
        val originalName = multipartFile.originalFilename!!
        val uploadedName = UUID.randomUUID().toString() + ext

        val metadata = ObjectMetadata().apply {
            contentLength = multipartFile.size
            contentType = multipartFile.contentType
        }

        try {
            amazonS3.putObject(bucket, uploadedName, multipartFile.inputStream, metadata)
        } catch (e: IOException) {
            throw AWSS3Exception(UPLOAD_IMAGE_FAIL)
        }

        return ProfileImage(originalName, uploadedName)
    }

    fun downloadOne(profileImage: ProfileImage): S3DownloadResult {
        if (!amazonS3.doesObjectExist(bucket, profileImage.uploadedName)) {
            throw AWSS3Exception(IMAGE_NOT_FOUND)
        }
        val url = "https://s3.$area.amazonaws.com/$bucket/${profileImage.uploadedName}"

        return S3DownloadResult.of(profileImage.id!!, profileImage.originalName, url)
    }

    fun deleteOne(uploadedName: String?) {
        try {
            amazonS3.deleteObject(bucket, uploadedName)
        } catch (e: Exception) {
            throw AWSS3Exception(DELETE_IMAGE_FAIL)
        }
    }
}
