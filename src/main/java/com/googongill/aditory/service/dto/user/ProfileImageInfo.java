package com.googongill.aditory.service.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageInfo {
    private Long profileImageId;
    private String originalName;
    private String url;
}
