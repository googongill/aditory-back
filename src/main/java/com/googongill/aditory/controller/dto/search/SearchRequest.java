package com.googongill.aditory.controller.dto.search;

import com.googongill.aditory.domain.enums.CategoryScope;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class SearchRequest {
    @Size(min = 2, max = 100, message = "검색어는 2자 이상 100자 이하로 입력해주세요.")
    private String query;
    private CategoryScope categoryScope;
}
