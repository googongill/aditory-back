package com.googongill.aditory.controller.dto.search

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.googongill.aditory.common.code.SearchErrorCode
import com.googongill.aditory.exception.SearchException
import com.googongill.aditory.service.dto.category.CategoryInfo
import com.googongill.aditory.service.dto.category.CategoryListResult
import com.googongill.aditory.service.dto.search.SearchResult

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class SearchResponse(
    var publicCategoryList: List<CategoryInfo> = emptyList(),
    var myCategoryList: List<CategoryInfo> = emptyList()
) {

    companion object {
        fun of(searchResult: SearchResult): SearchResponse {
            return if (searchResult is CategoryListResult) {
                SearchResponse(
                    publicCategoryList = searchResult.categoryList
                )
            } else if (searchResult is CategoryListResult) {
                SearchResponse(
                    myCategoryList = searchResult.categoryList
                )
            } else {
                throw SearchException(SearchErrorCode.INVALID_SEARCH_RESULT_TYPE)
            }
        }
    }
}
