package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeCategoryResult {
    private Long categoryId;
    private Integer likeCount;

    public static LikeCategoryResult of(Category category) {
        return LikeCategoryResult.builder()
                .categoryId(category.getId())
                .likeCount(category.getCategoryLikes().size())
                .build();
    }

}
