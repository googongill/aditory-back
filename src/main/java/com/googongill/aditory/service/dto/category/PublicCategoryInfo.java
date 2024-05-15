package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PublicCategoryInfo {
    private Long categoryId;
    private String categoryName;
    private String asCategoryName;
    private Integer linkCount;
    private CategoryState categoryState;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
