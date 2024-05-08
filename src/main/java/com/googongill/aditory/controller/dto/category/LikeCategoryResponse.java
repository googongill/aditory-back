package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.LikeCategoryResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LikeCategoryResponse {
    private Long categoryId;
    private Long likeCount;

    public static LikeCategoryResponse of(LikeCategoryResult likeCategoryResult) {
        return new LikeCategoryResponse(likeCategoryResult.getCategoryId(), likeCategoryResult.getLikeCount());
    }
}

