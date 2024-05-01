package com.googongill.aditory.controller.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCategoryRequest {
    @NotBlank
    private String categoryName;
    public Category toEntity() {
        return new Category(this.categoryName);

    }

}
