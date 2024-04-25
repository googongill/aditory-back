package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Builder;

@Builder
@JsonSerialize
public class UserTokenResponse {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("refreshToken")
    private String refreshToken;

    public static UserTokenResponse of(UserTokenResult userTokenResult) {
        return UserTokenResponse.builder()
                .userId(userTokenResult.getUserId())
                .nickname(userTokenResult.getNickname())
                .accessToken("Bearer " + userTokenResult.getAccessToken())
                .refreshToken("Bearer " + userTokenResult.getRefreshToken())
                .build();
    }
}
