package com.googongill.aditory.service;

import com.amazonaws.services.s3.AmazonS3;
import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.exception.AWSS3Exception;
import com.googongill.aditory.external.s3.AWSS3Service;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import static com.googongill.aditory.TestDataRepository.createProfileImage;
import static com.googongill.aditory.common.code.AWSS3ErrorCode.IMAGE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AWSS3ServiceTest {

    @InjectMocks
    private AWSS3Service awss3Service;
    @Mock
    private AmazonS3 amazonS3;
    @InjectMocks
    private TestDataRepository testDataRepository;

    @Test
    public void uploadOne_Success() throws Exception {
        // given
        MockMultipartFile mockMultipartFile = testDataRepository.createMockMultipartFile();
        ProfileImage targetResult = createProfileImage();

        given(amazonS3.putObject(any(), any(), any(), any())).willReturn(null);

        // when
        ProfileImage actualResult = awss3Service.uploadOne(mockMultipartFile);

        // then
        Assertions.assertThat(actualResult.getOriginalName()).isEqualTo(targetResult.getOriginalName());
    }

    @Test
    public void downloadOne_Success() throws Exception {
        // given
        ProfileImage profileImage = testDataRepository.createProfileImage();
        S3DownloadResult targetResult = testDataRepository.createS3DownloadResult();

        given(amazonS3.doesObjectExist(any(), any())).willReturn(true);

        // when
        S3DownloadResult actualResult = awss3Service.downloadOne(profileImage);

        // then
        Assertions.assertThat(actualResult.getOriginalName()).isEqualTo(targetResult.getOriginalName());
    }

    @Test
    public void downloadOne_Failed_With_NotExistingImage() throws Exception {
        // given
        ProfileImage profileImage = testDataRepository.createProfileImage();

        given(amazonS3.doesObjectExist(any(), any())).willReturn(false);

        // when
        AWSS3Exception exception = org.junit.jupiter.api.Assertions.assertThrows(AWSS3Exception.class, () -> awss3Service.downloadOne(profileImage));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(IMAGE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void deleteOne_Success() throws Exception {
        // given
        String uploadedName = "12345678.jpg";

        // when
        awss3Service.deleteOne(uploadedName);

        // then
        verify(amazonS3, times(1)).deleteObject(any(), any());
    }
}