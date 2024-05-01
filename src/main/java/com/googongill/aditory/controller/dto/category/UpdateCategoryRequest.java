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
    private CategoryState state;
    private String asCategoryName;

}

/*Request Body
{
		"categoryName": "새로운 개발",
		"state": "PUBLIC",
		"asCategoryName": "공유시 카테고리 이름"
		공유시 카테고리 이름?
}*/