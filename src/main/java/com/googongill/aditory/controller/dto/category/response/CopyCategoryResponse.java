package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CopyCategoryResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CopyCategoryResponse{
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private CategoryState categoryState;
    private LocalDateTime createdAt;

    public static CopyCategoryResponse of(CopyCategoryResult copyCategoryResult) {
        return CopyCategoryResponse.builder()
                .categoryId(copyCategoryResult.getCategoryId())
                .categoryName(copyCategoryResult.getCategoryName())
                .asCategoryName(copyCategoryResult.getAsCategoryName())
                .categoryState(copyCategoryResult.getCategoryState())
                .createdAt(copyCategoryResult.getCreatedAt())
                .build();
    }
}
