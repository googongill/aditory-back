package com.googongill.aditory.service.dto;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResult {
    private Long userId;
    private String nickname;

    public static SignupResult of(User user) {
        return SignupResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}