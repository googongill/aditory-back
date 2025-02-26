package com.googongill.aditory.service.dto.category

import com.googongill.aditory.service.dto.search.SearchResult

data class CategoryListResult(
    var categoryList: List<CategoryInfo> = emptyList()
) : SearchResult {

    companion object {
        fun of(categoryPublicList: List<CategoryInfo>): CategoryListResult {
            return CategoryListResult(
                categoryList = categoryPublicList
            )
        }
    }

}