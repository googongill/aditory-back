package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageResult {
    private Long userId;
    private String username;
    private String nickname;
    private ProfileImageInfo profileImageInfo;

    public static ProfileImageResult of(User user, ProfileImageInfo profileImageInfo) {
        return ProfileImageResult.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profileImageInfo(profileImageInfo)
                .build();
    }
}
