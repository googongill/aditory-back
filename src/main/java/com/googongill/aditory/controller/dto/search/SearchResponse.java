package com.googongill.aditory.controller.dto.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.exception.SearchException;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.search.SearchResult;;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import static com.googongill.aditory.common.code.SearchErrorCode.INVALID_SEARCH_RESULT_TYPE;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SearchResponse {
    @Builder.Default
    List<CategoryInfo> publicCategoryList = new ArrayList<>();
    @Builder.Default
    List<CategoryInfo> myCategoryList = new ArrayList<>();

    public static SearchResponse of(SearchResult searchResult) {

        if (searchResult instanceof CategoryListResult publicResult) {
            return SearchResponse.builder()
                    .publicCategoryList(publicResult.getCategoryList())
                    .build();
        } else if (searchResult instanceof CategoryListResult myResult) {
            return SearchResponse.builder()
                    .myCategoryList(myResult.getCategoryList())
                    .build();
        } else {
            throw new SearchException(INVALID_SEARCH_RESULT_TYPE);
        }
    }
}
