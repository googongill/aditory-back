package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.UnlikeCategoryResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UnlikeCategoryResponse {
    private Long categoryId;
    private Long likeCount;

    public UnlikeCategoryResponse(Long categoryId, Long likeCount) {
        this.categoryId = categoryId;
        this.likeCount = likeCount;
    }
    public static UnlikeCategoryResponse of(UnlikeCategoryResult unlikeCategoryResult) {
        return new UnlikeCategoryResponse(unlikeCategoryResult.getCategoryId(), unlikeCategoryResult.getLikeCount());
    }

}
