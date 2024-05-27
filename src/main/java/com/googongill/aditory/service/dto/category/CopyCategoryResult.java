package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CopyCategoryResult {
    private Long categoryId;
    private String categoryName;
    private CategoryState categoryState;
    private LocalDateTime createdAt;

    public static CopyCategoryResult of(Category newCategory) {
        return CopyCategoryResult.builder()
                .categoryId(newCategory.getId())
                .categoryName(newCategory.getCategoryName())
                .categoryState(newCategory.getCategoryState())
                .createdAt(newCategory.getCreatedAt())
                .build();
    }
}