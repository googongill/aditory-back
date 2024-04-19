package com.googongill.aditory.controller.dto.user;

import com.googongill.aditory.service.dto.SignupResult;
import lombok.Builder;

@Builder
public class SignupResponse {
    private Long userId;
    private String nickname;

    public static SignupResponse of(SignupResult signupResult) {
        return SignupResponse.builder()
                .userId(signupResult.getUserId())
                .nickname(signupResult.getNickname())
                .build();
    }
}
