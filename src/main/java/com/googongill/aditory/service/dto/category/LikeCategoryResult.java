package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeCategoryResult {
    private Long categoryId;
    private Integer likeCount;

    public static LikeCategoryResult of(Long categoryId, Integer likeCount) {
        return LikeCategoryResult.builder()
                .categoryId(categoryId)
                .likeCount(likeCount)
                .build();
    }

}
