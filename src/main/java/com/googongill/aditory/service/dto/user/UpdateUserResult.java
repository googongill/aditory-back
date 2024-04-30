package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserResult {
    private Long userId;
    private String nickname;
    private String contact;

    public static UpdateUserResult of(User user) {
        return UpdateUserResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .contact(user.getContact())
                .build();
    }
}
