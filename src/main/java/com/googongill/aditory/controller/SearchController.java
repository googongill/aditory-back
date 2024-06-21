package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.category.response.CategoryListResponse;
import com.googongill.aditory.controller.dto.link.response.LinkListResponse;
import com.googongill.aditory.controller.dto.search.SearchRequest;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.SuccessCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search/categories")
    public ResponseEntity<ApiResponse<CategoryListResponse>> searchCategories(@Valid @ModelAttribute SearchRequest searchRequest, Pageable pageable,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(CATEGORY_SEARCH_SUCCESS,
                CategoryListResponse.of(searchService.searchCategories(searchRequest, pageable, principalDetails.getUserId())));
    }

    @GetMapping("/search/links")
    public ResponseEntity<ApiResponse<LinkListResponse>> searchLinks(@Valid @ModelAttribute SearchRequest searchRequest, Pageable pageable,
                                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(LINK_SEARCH_SUCCESS,
                LinkListResponse.of(searchService.searchLinks(searchRequest, pageable, principalDetails.getUserId())));
    }

}
