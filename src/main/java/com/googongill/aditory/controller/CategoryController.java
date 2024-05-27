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
    private final CategoryRepository categoryRepository;
    private final CategoryLikeService categoryLikeService;

    // ======= Create =======

    //카테고리 저장
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_SUCCESS,
                CreateCategoryResponse.of(categoryService.createCategory(createCategoryRequest, principalDetails.getUserId())));
    }

    //카테고리 복사
    @PostMapping("/categories/{categoryId}/copy")
    public ResponseEntity<ApiResponse<CopyCategoryResponse>> copyCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(COPY_CATEGORY_SUCCESS,
                CopyCategoryResponse.of(categoryService.copyCategory(categoryId, principalDetails.getUserId())));
    }

    //카테고리 속 링크 이동
    @PostMapping("/categories/{categoryId}/move")
    public ResponseEntity<ApiResponse<MoveCategoryResponse>> moveCategory(@Valid @RequestBody MoveCategoryRequest moveCategoryRequest,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(MOVE_CATEGORY_SUCCESS,
                MoveCategoryResponse.of(categoryService.moveCategory(moveCategoryRequest,principalDetails.getUserId())));
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
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> getCategory(@PathVariable Long categoryId,
                                                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_SUCCESS,
                CategoryDetailResponse.of(categoryService.getCategory(categoryId, principalDetails.getUserId())));
    }

    // 내 카테고리 목록 조회
    @GetMapping("/categories/my")
    public ResponseEntity<ApiResponse<MyCategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_MY_CATEGORY_LIST_SUCCESS,
                MyCategoryListResponse.of(categoryService.getCategoryList(principalDetails.getUserId())));
    }

    //공개 카테고리 목록 조회
    @GetMapping("/categories/public")
    public ResponseEntity<ApiResponse<PublicCategoryListResponse>> getPublicCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_PUBLIC_CATEGORY_LIST_SUCCESS,
                PublicCategoryListResponse.of(categoryService.getPublicCategoryList(principalDetails.getUserId())));
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