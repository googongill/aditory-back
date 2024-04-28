package com.googongill.aditory.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryIdAndName;
import com.googongill.aditory.service.dto.user.SignResult;
import lombok.Builder;

import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SignResponse {
    private Long userId;
    private String nickname;
    private List<CategoryIdAndName> userCategories;

    public static SignResponse of(SignResult signResult) {
        return SignResponse.builder()
                .userId(signResult.getUserId())
                .nickname(signResult.getNickname())
                .userCategories(signResult.getUserCategories())
                .build();
    }
}
