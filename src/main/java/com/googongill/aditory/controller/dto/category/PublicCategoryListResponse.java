package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.PublicCategoryInfo;
import com.googongill.aditory.service.dto.category.PublicCategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PublicCategoryListResponse {
    @Builder.Default
    List<PublicCategoryInfo> categoryPublicList = new ArrayList<>();

    public static PublicCategoryListResponse of(PublicCategoryListResult publicCategoryListResult) {
        return PublicCategoryListResponse.builder()
                .categoryPublicList(publicCategoryListResult.getCategoryPublicList())
                .build();
    }
}
