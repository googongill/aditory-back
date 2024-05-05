package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.ProfileImageInfo;
import com.googongill.aditory.service.dto.user.ProfileImageResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProfileImageResponse {
    private Long userId;
    private String username;
    private String nickname;
    private ProfileImageInfo profileImageInfo;

    public static ProfileImageResponse of(ProfileImageResult profileImageResult) {
        return ProfileImageResponse.builder()
                .userId(profileImageResult.getUserId())
                .username(profileImageResult.getUsername())
                .nickname(profileImageResult.getNickname())
                .profileImageInfo(profileImageResult.getProfileImageInfo())
                .build();
    }
}
