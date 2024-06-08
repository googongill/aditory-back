package com.googongill.aditory.controller.dto.user.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserTokenResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String contact;
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private List<CategoryResult> userCategories = new ArrayList<>();


    public static UserTokenResponse of(UserTokenResult userTokenResult) {
        return UserTokenResponse.builder()
                .userId(userTokenResult.getUserId())
                .username(userTokenResult.getUsername())
                .nickname(userTokenResult.getNickname())
                .contact(userTokenResult.getContact())
                .accessToken(userTokenResult.getAccessToken())
                .refreshToken(userTokenResult.getRefreshToken())
                .userCategories(userTokenResult.getUserCategories())
                .build();
    }
}
