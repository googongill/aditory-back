package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.LoginResult;
import lombok.Builder;

@Builder
@JsonSerialize
public class LoginResponse {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("refreshToken")
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
