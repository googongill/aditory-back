package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.category.*;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.CategoryService;
import com.googongill.aditory.repository.CategoryRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RestController;

import static com.googongill.aditory.common.code.SuccessCode.*;
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.CategoryErrorCode.FORBIDDEN_CATEGORY;


@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    // ======= Create =======
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_SUCCESS,
                CreateCategoryResponse.of(categoryService.createCategory(createCategoryRequest, principalDetails.getUserId())));
    }

    // ======== Read ========

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getCategoryList(principalDetails.getUserId())));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId,
                                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_SUCCESS,
                CategoryResponse.of(categoryService.getCategory(categoryId, principalDetails.getUserId())));
    }
    @GetMapping("/categories/public")
    public ResponseEntity<ApiResponse<CategoryPublicListResponse>> getPublicCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_PUBLIC_LIST_SUCCESS,
                CategoryPublicListResponse.of(categoryService.getPublicCategoryList(principalDetails.getUserId())));
    }

    // ======= Update =======

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<UpdateCategoryResponse>> updateCategory(@PathVariable Long categoryId, @RequestBody UpdateCategoryRequest updateCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_CATEGORY_SUCCESS,
                UpdateCategoryResponse.of(categoryService.updateCategory(categoryId, updateCategoryRequest, principalDetails.getUserId())));
    }

    // ======= Delete =======

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<DeleteCategoryResponse>> deleteCategory(@PathVariable Long categoryId,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (!category.getUser().getId().equals(principalDetails.getUserId())) {
            throw new CategoryException(FORBIDDEN_CATEGORY);
        }
        categoryRepository.delete(category);
        return ApiResponse.success(DELETE_CATEGORY_SUCCESS,
                DeleteCategoryResponse.of(categoryId));
    }

}