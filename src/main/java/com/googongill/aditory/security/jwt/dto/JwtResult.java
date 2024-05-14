package com.googongill.aditory.security.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResult {
    private String accessToken;
    private String refreshToken;
}
