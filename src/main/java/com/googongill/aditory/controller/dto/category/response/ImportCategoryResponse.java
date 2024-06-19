package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.service.dto.category.CategoryResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ImportCategoryResponse {
    @Builder.Default
    private List<CategoryResult> userCategories = new ArrayList<>();

    public static ImportCategoryResponse of(List<Category> categories) {
        return ImportCategoryResponse.builder()
                .userCategories(categories.stream()
                        .map(category -> CategoryResult.builder()
                                .categoryId(category.getId())
                                .categoryName(category.getCategoryName())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
