package com.googongill.aditory.controller.dto.search;

import com.googongill.aditory.domain.enums.CategoryScope;
import com.googongill.aditory.domain.enums.SearchType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class SearchRequest {
    private String query;
    private SearchType searchType;
    private CategoryScope categoryScope;
}
