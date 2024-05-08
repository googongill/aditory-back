package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.controller.dto.category.GetCategoryLikeResponse;
import com.googongill.aditory.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCategoryLikeResult {

    private Long categoryId;
    private int likeCount;
    public static GetCategoryLikeResult of(Category category) {
        return GetCategoryLikeResult.builder()
                .categoryId(category.getId())
                .likeCount(category.getTotalLikeCount())
                .build();
    }
}
