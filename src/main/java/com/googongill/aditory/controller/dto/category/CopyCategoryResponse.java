package com.googongill.aditory.controller.dto.category;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.service.dto.category.CopyCategoryResult;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CopyCategoryResponse{
    private Long categoryId;
    private String categoryName;
    private CategoryState categoryState;
    private String createdAt;

    public static CopyCategoryResponse of(CopyCategoryResult copyCategoryResult) {
        return CopyCategoryResponse.builder()
                .categoryId(copyCategoryResult.getCategoryId())
                .categoryName(copyCategoryResult.getCategoryName())
                .categoryState(copyCategoryResult.getCategoryState())
                .createdAt(copyCategoryResult.getCreatedAt())
                .build();
    }


}



/*
Response Body
{
        "httpStatus": 201,
        "message": "카테고리 복사에 성공했습니다.",
        "success": true,
        "data": {
        "categoryId": 1,
        "categoryName": "학술",
        "categoryState": "PRIVATE",
        "createdAt": "2024-04-15T10:00:00Z"
        }
        }
Http Status: 201 Created
*/