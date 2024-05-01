package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateCategoryResult {
    private Long categoryId;
    private String categoryName;
    //private String asCategoryName;
    private CategoryState state;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    public static UpdateCategoryResult of(Category category) {
        return UpdateCategoryResult.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .state(category.getState())
                .createdAt(category.getCreatedAt())
                .lastModifiedAt(category.getLastModifiedAt())
                .build();
    }
}
