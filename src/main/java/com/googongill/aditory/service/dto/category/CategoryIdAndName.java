package com.googongill.aditory.service.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryIdAndName {
    private Long categoryId;
    private String categoryName;
}
