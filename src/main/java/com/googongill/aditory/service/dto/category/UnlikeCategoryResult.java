package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnlikeCategoryResult {
    private Long categoryId;
    private Long likeCount;

    public static UnlikeCategoryResult of(Long categoryId, Long likeCount) {
        return UnlikeCategoryResult.builder()
                .categoryId(categoryId)
                .likeCount(likeCount)
                .build();
    }

}
