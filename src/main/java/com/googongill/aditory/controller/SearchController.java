package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.search.SearchRequest;
import com.googongill.aditory.controller.dto.search.SearchResponse;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.googongill.aditory.common.code.SuccessCode.SEARCH_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<SearchResponse>> searchTitle(@RequestBody SearchRequest searchRequest,
                                                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
    return ApiResponse.success(SEARCH_SUCCESS,
            SearchResponse.of(searchService.search(searchRequest, principalDetails.getUserId())));
    }
}
