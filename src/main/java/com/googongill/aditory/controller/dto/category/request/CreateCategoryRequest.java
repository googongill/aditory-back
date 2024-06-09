package com.googongill.aditory.controller.dto.category.request;

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
        return new Category(this.categoryName, this.categoryName, user);
    }
}
