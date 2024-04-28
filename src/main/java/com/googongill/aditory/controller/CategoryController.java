package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.category.CategoryListResponse;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.googongill.aditory.common.code.SuccessCode.GET_CATEGORY_LIST_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ======= Create =======


    // ======== Read ========

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getCategoryList(principalDetails.getUserId())));
    }

    // ======= Update =======


    // ======= Delete =======


}