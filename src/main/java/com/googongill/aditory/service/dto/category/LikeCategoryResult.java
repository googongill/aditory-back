package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeCategoryResult {
    private Long categoryId;
    private Long likeCount;

    public static LikeCategoryResult of(Long categoryId, Long likeCount) {
        return LikeCategoryResult.builder()
                .categoryId(categoryId)
                .likeCount(likeCount)
                .build();
    }

}
