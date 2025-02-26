package com.googongill.aditory.service.dto.category

data class LikeCategoryListResult(
    var likeCategoryList: List<Long> = emptyList()
) {

    companion object {
        fun of(likeCategoryList: List<Long>): LikeCategoryListResult {
            return LikeCategoryListResult(
                likeCategoryList = likeCategoryList
            )
        }
    }
}
