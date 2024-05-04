package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;
@Getter
@Builder
public class CreateCategoryResult {
    private Long categoryId;
    private String categoryName;
    private CategoryState categoryState;
    private LocalDateTime createdAt;

    public static CreateCategoryResult of(Category category) {
        return CreateCategoryResult.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryState(category.getCategoryState())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
