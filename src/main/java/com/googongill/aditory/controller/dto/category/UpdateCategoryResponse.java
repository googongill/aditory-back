package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.UpdateCategoryResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdateCategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private CategoryState categoryState;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static UpdateCategoryResponse of(UpdateCategoryResult updateCategoryResult) {
        return UpdateCategoryResponse.builder()
                .categoryId(updateCategoryResult.getCategoryId())
                .categoryName(updateCategoryResult.getCategoryName())
                .asCategoryName(updateCategoryResult.getAsCategoryName())
                .categoryState(updateCategoryResult.getCategoryState())
                .createdAt(updateCategoryResult.getCreatedAt())
                .lastModifiedAt(updateCategoryResult.getLastModifiedAt())
                .build();
    }
}
