package com.googongill.aditory.external.s3.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3DownloadResult {
    private Long profileImageId;
    private String originalName;
    private String url;

    public static S3DownloadResult of(Long profileImageId, String originalName, String url) {
        return S3DownloadResult.builder()
                .profileImageId(profileImageId)
                .originalName(originalName)
                .url(url)
                .build();
    }
}
