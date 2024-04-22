package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.SignupResult;
import lombok.Builder;

@Builder
@JsonSerialize
public class SignupResponse {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("nickname")
    private String nickname;

    public static SignupResponse of(SignupResult signupResult) {
        return SignupResponse.builder()
                .userId(signupResult.getUserId())
                .nickname(signupResult.getNickname())
                .build();
    }
}
