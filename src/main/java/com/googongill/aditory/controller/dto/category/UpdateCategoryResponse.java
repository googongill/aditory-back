package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.UpdateCategoryResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdateCategoryResponse {
    private Long categoryId;
    private String categoryName;
    //private String asCategoryName;
    private CategoryState state;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static UpdateCategoryResponse of(UpdateCategoryResult updateCategoryResult) {
        return UpdateCategoryResponse.builder()
                .categoryId(updateCategoryResult.getCategoryId())
                .categoryName(updateCategoryResult.getCategoryName())
                .state(updateCategoryResult.getState())
                .createdAt(updateCategoryResult.getCreatedAt())
                .lastModifiedAt(updateCategoryResult.getLastModifiedAt())
                .build();

    }
}


/*Response Body
{
		"httpStatus": 200,
		"message": "카테고리 수정에 성공했습니다.",
		"success": true,
		"data": {
				"categoryId": 1,
				"categoryName": "새로운 개발",
				"adCategoryName": "공유시 카테고리 이름",
				"state": "PUBLIC",
				"createdAt": "2024-04-15T10:00:00Z",
				"lastModifiedAt": "2024-04-15T12:00:00Z"
		}
}*/