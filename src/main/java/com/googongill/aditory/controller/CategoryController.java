package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.category.request.CreateCategoryRequest;
import com.googongill.aditory.controller.dto.category.request.MoveCategoryRequest;
import com.googongill.aditory.controller.dto.category.request.UpdateCategoryRequest;
import com.googongill.aditory.controller.dto.category.response.*;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.CategoryLikeService;
import com.googongill.aditory.service.CategoryService;
import com.googongill.aditory.repository.CategoryRepository;

import com.googongill.aditory.service.dto.category.CategoryInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    // 카테고리 저장
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_SUCCESS,
                CreateCategoryResponse.of(categoryService.createCategory(createCategoryRequest, principalDetails.getUserId())));
    }

    // 카테고리 복사
    @PostMapping("/categories/{categoryId}/copy")
    public ResponseEntity<ApiResponse<CopyCategoryResponse>> copyCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(COPY_CATEGORY_SUCCESS,
                CopyCategoryResponse.of(categoryService.copyCategory(categoryId, principalDetails.getUserId())));
    }

    // 카테고리 속 링크 이동
    @PostMapping("/categories/{categoryId}/move")
    public ResponseEntity<ApiResponse<MoveCategoryResponse>> moveCategory(@PathVariable Long categoryId,
                                                                          @Valid @RequestBody MoveCategoryRequest moveCategoryRequest,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(MOVE_CATEGORY_SUCCESS,
                MoveCategoryResponse.of(categoryService.moveCategory(categoryId, moveCategoryRequest,principalDetails.getUserId())));
    }

    // 좋아요
    @PostMapping("/categories/{categoryId}/like")
    public ResponseEntity<ApiResponse<LikeCategoryResponse>> likeCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_LIKE_SUCCESS,
                LikeCategoryResponse.of(categoryLikeService.likeCategory(categoryId, principalDetails.getUserId())));
    }

    @PostMapping("/categories/import")
    public ResponseEntity<ApiResponse<Object>> importCategories(@RequestParam MultipartFile importFile,
                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        categoryService.importCategories(importFile, principalDetails.getUserId());
        return ApiResponse.success(IMPORT_CATEGORY_SUCCESS);
    }

    // ======== Read ========
  
    // 카테고리 상세 조회
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> getCategoryDetail(@PathVariable Long categoryId,
                                                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_SUCCESS,
                CategoryDetailResponse.of(categoryService.getCategoryDetail(categoryId, principalDetails.getUserId())));
    }

    // 내 카테고리 목록 조회
    @GetMapping("/categories/my")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_MY_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getMyCategoryList(principalDetails.getUserId())));
    }

    // 공개 카테고리 목록 전체 조회
    @GetMapping("/categories/public/all")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getPublicCategories(Pageable pageable,
                                                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Page<CategoryInfo> categoryInfoPage = categoryService.getPublicCategoryList(pageable, principalDetails.getUserId());
        return ApiResponse.success(GET_PUBLIC_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryInfoPage));
    }

    // 오늘의 추천 공개 카테고리 목록 조회
    @GetMapping("/categories/public/today")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getTodayPublicCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_TODAY_PUBLIC_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getTodayPublicCategoryList(principalDetails.getUserId())));
    }

    @GetMapping("/categories/like")
    public ResponseEntity<ApiResponse<LikeCategoryListResponse>> getLikeCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_LIKE_CATEGORY_LIST_SUCCESS,
                LikeCategoryListResponse.of(categoryLikeService.getLikeCategoryList(principalDetails.getUserId())));
    }

    // ======= Update =======

    // 카테고리 수정
    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<UpdateCategoryResponse>> updateCategory(@PathVariable Long categoryId, @RequestBody UpdateCategoryRequest updateCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_CATEGORY_SUCCESS,
                UpdateCategoryResponse.of(categoryService.updateCategory(categoryId, updateCategoryRequest, principalDetails.getUserId())));
    }

    // ======= Delete =======

    // 카테고리 삭제
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