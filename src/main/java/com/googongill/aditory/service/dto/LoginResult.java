package com.googongill.aditory.service.dto;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResult {
    private Long userId;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public static LoginResult of(User user, String accessToken, String refreshToken) {
        return LoginResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
