package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.LikeCategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LikeCategoryListResponse {
    @Builder.Default
    List<Long> likeCategoryList = new ArrayList<>();

    public static LikeCategoryListResponse of(LikeCategoryListResult likeCategoryListResult) {
        return LikeCategoryListResponse.builder()
                .likeCategoryList(likeCategoryListResult.getLikeCategoryList())
                .build();
    }
}
