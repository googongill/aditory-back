package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignResult {
    private Long userId;
    private String nickname;

    public static SignResult of(User user) {
        return SignResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}