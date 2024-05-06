package com.googongill.aditory.service.dto.user;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.service.dto.category.CategoryIdAndName;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SignResult {
    private Long userId;
    private String nickname;
    private List<CategoryIdAndName> userCategories = new ArrayList<>();

    public static SignResult of(User user, List<Category> categories) {
        List<CategoryIdAndName> userCategories = categories.stream()
                .map(category -> CategoryIdAndName.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .build())
                .collect(Collectors.toList());

        return SignResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .userCategories(userCategories)
                .build();
    }
}