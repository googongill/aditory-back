package com.googongill.aditory.controller.dto.user;

import com.googongill.aditory.service.dto.LoginResult;
import lombok.Builder;

@Builder
public class LoginResponse {
    private Long userId;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(LoginResult loginResult) {
        return LoginResponse.builder()
                .userId(loginResult.getUserId())
                .nickname(loginResult.getNickname())
                .accessToken(loginResult.getAccessToken())
                .refreshToken(loginResult.getRefreshToken())
                .build();
    }
}
