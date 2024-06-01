package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.security.jwt.dto.JwtResult;
import com.googongill.aditory.service.dto.category.CategoryResult;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserTokenResult {
    private Long userId;
    private String username;
    private String nickname;
    private String contact;
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private List<CategoryResult> userCategories = new ArrayList<>();

    public static UserTokenResult of(User user, JwtResult jwtResult) {
        List<CategoryResult> userCategories = user.getCategories().stream()
                .map(category -> CategoryResult.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .build())
                .collect(Collectors.toList());

        return UserTokenResult.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .contact(user.getContact())
                .accessToken(jwtResult.getAccessToken())
                .refreshToken(jwtResult.getRefreshToken())
                .userCategories(userCategories)
                .build();
    }
}
