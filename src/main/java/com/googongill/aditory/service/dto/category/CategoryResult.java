package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResult {
    private Long categoryId;
    private String categoryName;
}
