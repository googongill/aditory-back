package com.googongill.aditory.controller.dto.user.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialLoginRequest {
    private String provider;
    private String code;
}
