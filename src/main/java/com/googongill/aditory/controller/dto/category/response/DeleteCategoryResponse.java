package com.googongill.aditory.controller.dto.category.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DeleteCategoryResponse {
    private Long categoryId;

    public static DeleteCategoryResponse of(Long deletedCategoryId) {
        return DeleteCategoryResponse.builder()
                .categoryId(deletedCategoryId)
                .build();
    }
}
