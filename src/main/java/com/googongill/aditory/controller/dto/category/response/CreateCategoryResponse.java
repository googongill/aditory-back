package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CreateCategoryResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CreateCategoryResponse {
    private Long categoryId;
    private String categoryName;
    private CategoryState categoryState;
    private LocalDateTime createdAt;

    public static CreateCategoryResponse of(CreateCategoryResult createCategoryResult) {
        return CreateCategoryResponse.builder()
                .categoryId(createCategoryResult.getCategoryId())
                .categoryName(createCategoryResult.getCategoryName())
                .categoryState(createCategoryResult.getCategoryState())
                .createdAt(createCategoryResult.getCreatedAt())
                .build();
    }
}