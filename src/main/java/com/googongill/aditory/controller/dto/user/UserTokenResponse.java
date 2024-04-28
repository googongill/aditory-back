package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserTokenResponse {
    private Long userId;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public static UserTokenResponse of(UserTokenResult userTokenResult) {
        return UserTokenResponse.builder()
                .userId(userTokenResult.getUserId())
                .nickname(userTokenResult.getNickname())
                .accessToken(userTokenResult.getAccessToken())
                .refreshToken(userTokenResult.getRefreshToken())
                .build();
    }
}
