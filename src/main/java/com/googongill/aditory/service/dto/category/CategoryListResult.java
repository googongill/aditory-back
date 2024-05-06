package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class CategoryListResult {
    @Builder.Default
    List<CategoryInfo> categoryList = new ArrayList<>();

    public static CategoryListResult of(List<CategoryInfo> categoryList) {
        return CategoryListResult.builder()
                .categoryList(categoryList)
                .build();
    }
}
