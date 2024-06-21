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

import java.util.List;

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

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_SUCCESS,
                CreateCategoryResponse.of(categoryService.createCategory(createCategoryRequest, principalDetails.getUserId())));
    }

    @PostMapping("/categories/{categoryId}/copy")
    public ResponseEntity<ApiResponse<CopyCategoryResponse>> copyCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(COPY_CATEGORY_SUCCESS,
                CopyCategoryResponse.of(categoryService.copyCategory(categoryId, principalDetails.getUserId())));
    }

    @PostMapping("/categories/{categoryId}/move")
    public ResponseEntity<ApiResponse<MoveCategoryResponse>> moveCategory(@PathVariable Long categoryId,
                                                                          @Valid @RequestBody MoveCategoryRequest moveCategoryRequest,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(MOVE_CATEGORY_SUCCESS,
                MoveCategoryResponse.of(categoryService.moveCategory(categoryId, moveCategoryRequest,principalDetails.getUserId())));
    }

    @PostMapping("/categories/{categoryId}/like")
    public ResponseEntity<ApiResponse<LikeCategoryResponse>> likeCategory(@PathVariable Long categoryId,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_CATEGORY_LIKE_SUCCESS,
                LikeCategoryResponse.of(categoryLikeService.likeCategory(categoryId, principalDetails.getUserId())));
    }

    @PostMapping("/categories/import")
    public ResponseEntity<ApiResponse<ImportCategoryResponse>> importCategories(@RequestParam MultipartFile importFile,
                                                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Category> categories = categoryService.importCategories(importFile, principalDetails.getUserId());
        return ApiResponse.success(IMPORT_CATEGORY_SUCCESS,
                ImportCategoryResponse.of(categories));
    }

    // ======== Read ========
  
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> getCategoryDetail(@PathVariable Long categoryId,
                                                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_CATEGORY_SUCCESS,
                CategoryDetailResponse.of(categoryService.getCategoryDetail(categoryId, principalDetails.getUserId())));
    }

    @GetMapping("/categories/my")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_MY_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryService.getMyCategoryList(principalDetails.getUserId())));
    }

    @GetMapping("/categories/public/all")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getPublicCategories(Pageable pageable,
                                                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Page<CategoryInfo> categoryInfoPage = categoryService.getPublicCategoryList(pageable, principalDetails.getUserId());
        return ApiResponse.success(GET_PUBLIC_CATEGORY_LIST_SUCCESS,
                CategoryListResponse.of(categoryInfoPage));
    }

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
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        categoryRepository.delete(category);
        return ApiResponse.success(DELETE_CATEGORY_SUCCESS,
                DeleteCategoryResponse.of(categoryId));
    }

    @DeleteMapping("/categories/{categoryId}/like")
    public ResponseEntity<ApiResponse<LikeCategoryResponse>> unlikeCategory(@PathVariable Long categoryId,
                                                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(DELETE_CATEGORY_LIKE_SUCCESS,
                LikeCategoryResponse.of(categoryLikeService.unlikeCategory(categoryId, principalDetails.getUserId())));
    }

}