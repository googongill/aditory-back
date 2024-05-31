package com.googongill.aditory.controller.dto.category;

import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateCategoryRequest {
    @NotBlank
    private String categoryName;

    public Category toEntity(User user) {
        String asCategoryName = "(default)";
        return new Category(this.categoryName, asCategoryName, user);
    }
}
