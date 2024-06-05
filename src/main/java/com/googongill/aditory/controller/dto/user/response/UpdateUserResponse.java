package com.googongill.aditory.controller.dto.user.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.user.UpdateUserResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdateUserResponse {
    private Long userId;
    private String nickname;
    private String contact;

    public static UpdateUserResponse of(UpdateUserResult updateUserResult) {
        return UpdateUserResponse.builder()
                .userId(updateUserResult.getUserId())
                .nickname(updateUserResult.getNickname())
                .contact(updateUserResult.getContact())
                .build();
    }
}
