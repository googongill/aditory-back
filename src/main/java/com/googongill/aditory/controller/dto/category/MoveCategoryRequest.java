package com.googongill.aditory.controller.dto.category;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class MoveCategoryRequest {
    private List<Long> linkIdList;
    private Long targetCategoryId;
}
