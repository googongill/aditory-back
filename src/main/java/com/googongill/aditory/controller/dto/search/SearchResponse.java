package com.googongill.aditory.controller.dto.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googongill.aditory.exception.SearchException;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import com.googongill.aditory.service.dto.category.MyCategoryInfo;
import com.googongill.aditory.service.dto.category.PublicCategoryInfo;
import com.googongill.aditory.service.dto.search.SearchResult;;
import com.googongill.aditory.service.dto.category.PublicCategoryListResult;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import static com.googongill.aditory.common.code.SearchErrorCode.INVALID_SEARCH_RESULT_TYPE;

@Builder
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SearchResponse {
    @Builder.Default
    List<PublicCategoryInfo> publicCategoryList = new ArrayList<>();
    @Builder.Default
    List<MyCategoryInfo> myCategoryList = new ArrayList<>();

    public static SearchResponse of(SearchResult searchResult) {

        if (searchResult instanceof PublicCategoryListResult publicResult) {
            return SearchResponse.builder()
                    .publicCategoryList(publicResult.getPublicCategoryList())
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
