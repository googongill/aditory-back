package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SignoutResponse {
    private Long userId;
    private String username;

    public static SignoutResponse of(Long userId, String username) {
        return SignoutResponse.builder()
                .userId(userId)
                .username(username)
                .build();
    }
}
