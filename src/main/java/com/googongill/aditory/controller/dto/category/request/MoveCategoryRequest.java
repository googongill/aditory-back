package com.googongill.aditory.controller.dto.category.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class MoveCategoryRequest {
    private List<Long> linkIdList;
    private Long targetCategoryId;
}
