package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import com.googongill.aditory.service.dto.user.ProfileImageResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProfileImageResponse {
    private Long userId;
    private String username;
    private String nickname;
    private S3DownloadResult s3DownloadResult;

    public static ProfileImageResponse of(ProfileImageResult profileImageResult) {
        return ProfileImageResponse.builder()
                .userId(profileImageResult.getUserId())
                .username(profileImageResult.getUsername())
                .nickname(profileImageResult.getNickname())
                .s3DownloadResult(profileImageResult.getS3DownloadResult())
                .build();
    }

    public static ProfileImageResponse of(User user, S3DownloadResult s3DownloadResult) {
        return ProfileImageResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .s3DownloadResult(s3DownloadResult)
                .build();
    }
}
