package com.googongill.aditory.controller

import com.googongill.aditory.common.ApiResponse
import com.googongill.aditory.common.code.SuccessCode.*
import com.googongill.aditory.controller.dto.category.response.CategoryListResponse
import com.googongill.aditory.controller.dto.link.response.LinkListResponse
import com.googongill.aditory.controller.dto.search.SearchRequest
import com.googongill.aditory.security.jwt.user.PrincipalDetails
import com.googongill.aditory.service.SearchService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    private val searchService: SearchService
) {

    // ====================================
    @GetMapping("/search/categories")
    fun searchCategories(
        @ModelAttribute @Valid searchRequest: SearchRequest,
        pageable: Pageable,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CategoryListResponse>> {
        return ApiResponse.success(
            CATEGORY_SEARCH_SUCCESS,
            CategoryListResponse.of(searchService.searchCategories(searchRequest, pageable, principalDetails.userId))
        )
    }

    @GetMapping("/search/links")
    fun searchLinks(
        @ModelAttribute @Valid searchRequest: SearchRequest,
        pageable: Pageable,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LinkListResponse>> {
        return ApiResponse.success(
            LINK_SEARCH_SUCCESS,
            LinkListResponse.of(searchService.searchLinks(searchRequest, pageable, principalDetails.userId))
        )
    }

}
