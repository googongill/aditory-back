package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;


@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryListResponse {
    List<CategoryInfo> categoryList = new ArrayList<>();

    public static CategoryListResponse of(CategoryListResult categoryListResult) {
        return CategoryListResponse.builder()
                .categoryList(categoryListResult.getCategoryList())
                .build();
    }
}
