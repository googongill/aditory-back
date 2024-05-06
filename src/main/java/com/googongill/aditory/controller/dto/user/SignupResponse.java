package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryIdAndName;
import com.googongill.aditory.service.dto.user.SignupResult;
import lombok.Builder;

import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SignupResponse {
    private Long userId;
    private String nickname;
    private List<CategoryIdAndName> userCategories;

    public static SignupResponse of(SignupResult signupResult) {
        return SignupResponse.builder()
                .userId(signupResult.getUserId())
                .nickname(signupResult.getNickname())
                .userCategories(signupResult.getUserCategories())
                .build();
    }
}
