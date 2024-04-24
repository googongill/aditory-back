package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.SignResult;
import lombok.Builder;

@Builder
@JsonSerialize
public class SignResponse {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("nickname")
    private String nickname;

    public static SignResponse of(SignResult signResult) {
        return SignResponse.builder()
                .userId(signResult.getUserId())
                .nickname(signResult.getNickname())
                .build();
    }
}