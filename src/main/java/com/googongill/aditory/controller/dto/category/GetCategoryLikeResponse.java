package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.GetCategoryLikeResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GetCategoryLikeResponse {
    private Long categoryId;
    private int likeCount;

    public GetCategoryLikeResponse(Long categoryId, int likeCount) {
        this.categoryId = categoryId;
        this.likeCount = likeCount;
    }
    public static GetCategoryLikeResponse of(GetCategoryLikeResult getCategoryLikeResult) {
        return new GetCategoryLikeResponse(getCategoryLikeResult.getCategoryId(), getCategoryLikeResult.getLikeCount());
    }
}
