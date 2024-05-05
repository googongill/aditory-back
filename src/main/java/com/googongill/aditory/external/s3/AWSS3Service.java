package com.googongill.aditory.external.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.exception.AWSS3Exception;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.googongill.aditory.common.code.AWSS3ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3Service {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String area;

    public ProfileImage uploadOne(MultipartFile multipartFile) {
        return upload(multipartFile);
    }

    private ProfileImage upload(MultipartFile multipartFile) {
        String ext = "." + multipartFile.getOriginalFilename().split("\\.(?=[^\\.]+$)")[1];
        String originalName = multipartFile.getOriginalFilename();
        String uploadedName = UUID.randomUUID() + ext;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, uploadedName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new AWSS3Exception(UPLOAD_IMAGE_FAIL);
        }
        return new ProfileImage(originalName, uploadedName);
    }

    public S3DownloadResult downloadOne(ProfileImage profileImage) {
        if (!amazonS3.doesObjectExist(bucket, profileImage.getUploadedName())) {
            throw new AWSS3Exception(IMAGE_NOT_FOUND);
        }
        String url = "https://s3." + area + ".amazonaws.com/" + bucket + "/" + profileImage.getUploadedName();
        return S3DownloadResult.of(profileImage.getId(), profileImage.getOriginalName(), url);
    }

    public void deleteOne(String uploadedName) {
        try {
            amazonS3.deleteObject(bucket, uploadedName);
        } catch (Exception e) {
            throw new AWSS3Exception(DELETE_IMAGE_FAIL);
        }
    }
}
