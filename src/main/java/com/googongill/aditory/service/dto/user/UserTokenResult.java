package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTokenResult {
    private Long userId;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public static UserTokenResult of(User user, String accessToken, String refreshToken) {
        return UserTokenResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
