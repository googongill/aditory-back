package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.category.*;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.CategoryLikeService;
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
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_FORBIDDEN;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryLikeService categoryLikeService;
    private final CategoryRepository categoryRepository;

    // ======= Create =======
    //카테고리 저장
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_SUCCESS,
                CreateCategoryResponse.of(categoryService.createCategory(createCategoryRequest, principalDetails.getUserId())));
    }
    //좋아요
    @PostMapping("/categories/{categoryId}/like")
    public ResponseEntity<ApiResponse<LikeCategoryResponse>> likeCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_LIKE_SUCCESS,
                LikeCategoryResponse.of(categoryLikeService.likeCategory(categoryId, principalDetails.getUserId())));
    }

    // ======== Read ========
  
    // 카테고리 조회
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId,
                                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_SUCCESS,
                CategoryResponse.of(categoryService.getCategory(categoryId, principalDetails.getUserId())));
    }
    // 내 카테고리 목록 조회
    @GetMapping("/categories/my")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_MY_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getCategoryList(principalDetails.getUserId())));
    }
    //공개 카테고리 목록 조회
    @GetMapping("/categories/public")
    public ResponseEntity<ApiResponse<CategoryPublicListResponse>> getPublicCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_PUBLIC_CATEGORY_LIST_SUCCESS,
                CategoryPublicListResponse.of(categoryService.getPublicCategoryList(principalDetails.getUserId())));
    }

    // ======= Update =======
    //카테고리 수정
    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<UpdateCategoryResponse>> updateCategory(@PathVariable Long categoryId, @RequestBody UpdateCategoryRequest updateCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_CATEGORY_SUCCESS,
                UpdateCategoryResponse.of(categoryService.updateCategory(categoryId, updateCategoryRequest, principalDetails.getUserId())));
    }

    // ======= Delete =======
    //카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<DeleteCategoryResponse>> deleteCategory(@PathVariable Long categoryId,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (!category.getUser().getId().equals(principalDetails.getUserId())) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        categoryRepository.delete(category);
        return ApiResponse.success(DELETE_CATEGORY_SUCCESS,
                DeleteCategoryResponse.of(categoryId));
    }
    // 좋아요 취소
    @DeleteMapping("/categories/{categoryId}/like")
    public ResponseEntity<ApiResponse<LikeCategoryResponse>> unlikeCategory(@PathVariable Long categoryId,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(DELETE_CATEGORY_LIKE_SUCCESS,
                LikeCategoryResponse.of(categoryLikeService.unlikeCategory(categoryId, principalDetails.getUserId())));
    }
}