package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.MyCategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;


@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MyCategoryListResponse {
    @Builder.Default
    List<MyCategoryInfo> categoryList = new ArrayList<>();

    public static MyCategoryListResponse of(CategoryListResult categoryListResult) {
        return MyCategoryListResponse.builder()
                .categoryList(categoryListResult.getCategoryList())
                .build();
    }
}
