package com.googongill.aditory.controller.dto.category;

import com.googongill.aditory.domain.enums.CategoryState;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCategoryRequest {
    @NotNull
    private String categoryName;
    private CategoryState categoryState;
    private String asCategoryName;
}