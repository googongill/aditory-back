package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.User;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageResult {
    private Long userId;
    private String username;
    private String nickname;
    private S3DownloadResult s3DownloadResult;

    public static ProfileImageResult of(User user, S3DownloadResult s3DownloadResult) {
        return ProfileImageResult.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .s3DownloadResult(s3DownloadResult)
                .build();
    }
}
