package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryPublicInfo;
import com.googongill.aditory.service.dto.category.CategoryPublicListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryPublicListResponse {
    List<CategoryPublicInfo> categoryPublicList = new ArrayList<>();

    public static CategoryPublicListResponse of(CategoryPublicListResult categoryPublicListResult) {
        return CategoryPublicListResponse.builder()
                .categoryPublicList(categoryPublicListResult.getCategoryPublicList())
                .build();
    }
}
