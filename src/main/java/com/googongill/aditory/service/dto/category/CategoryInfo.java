package com.googongill.aditory.service.dto.category;

import com.googongill.aditory.domain.enums.CategoryState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryInfo {
    private Long categoryId;
    private String categoryName;
    private Integer linkCount;
    private CategoryState state;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
