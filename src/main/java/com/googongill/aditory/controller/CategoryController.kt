package com.googongill.aditory.controller

import com.googongill.aditory.common.ApiResponse
import com.googongill.aditory.common.code.CategoryErrorCode.*
import com.googongill.aditory.common.code.SuccessCode.*
import com.googongill.aditory.controller.dto.category.request.CreateCategoryRequest
import com.googongill.aditory.controller.dto.category.request.MoveCategoryRequest
import com.googongill.aditory.controller.dto.category.request.UpdateCategoryRequest
import com.googongill.aditory.controller.dto.category.response.*
import com.googongill.aditory.domain.Category
import com.googongill.aditory.exception.CategoryException
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.security.jwt.user.PrincipalDetails
import com.googongill.aditory.service.CategoryLikeService
import com.googongill.aditory.service.CategoryService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class CategoryController(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val categoryLikeService: CategoryLikeService
) {

    // ======= Create =======
    // 카테고리 저장
    @PostMapping("/categories")
    fun createCategory(
        @RequestBody @Valid createCategoryRequest: CreateCategoryRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CreateCategoryResponse>> {
        return ApiResponse.success(
            SAVE_CATEGORY_SUCCESS,
            CreateCategoryResponse.of(
                categoryService.createCategory(createCategoryRequest, principalDetails.userId)
            )
        )
    }

    // 카테고리 복사
    @PostMapping("/categories/{categoryId}/copy")
    fun copyCategory(
        @PathVariable categoryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CopyCategoryResponse>> {
        return ApiResponse.success(
            COPY_CATEGORY_SUCCESS,
            CopyCategoryResponse.of(
                categoryService.copyCategory(categoryId, principalDetails.userId)
            )
        )
    }

    // 카테고리 속 링크 이동
    @PostMapping("/categories/{categoryId}/move")
    fun moveCategory(
        @PathVariable categoryId: Long,
        @RequestBody @Valid moveCategoryRequest: MoveCategoryRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<MoveCategoryResponse>> {
        return ApiResponse.success(
            MOVE_CATEGORY_SUCCESS,
            MoveCategoryResponse.of(
                categoryService.moveCategory(categoryId, moveCategoryRequest, principalDetails.userId)
            )
        )
    }

    // 좋아요
    @PostMapping("/categories/{categoryId}/like")
    fun likeCategory(
        @PathVariable categoryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LikeCategoryResponse>> {
        return ApiResponse.success(
            SAVE_CATEGORY_LIKE_SUCCESS,
            LikeCategoryResponse.of(
                categoryLikeService.likeCategory(categoryId, principalDetails.userId)
            )
        )
    }

    @PostMapping("/categories/import")
    fun importCategories(
        @RequestParam importFile: MultipartFile,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<ImportCategoryResponse>> {
        return ApiResponse.success(
            IMPORT_CATEGORY_SUCCESS,
            ImportCategoryResponse.of(
                categoryService.importCategories(importFile, principalDetails.userId)
            )
        )
    }

    // ======== Read ========
    // 카테고리 상세 조회
    @GetMapping("/categories/{categoryId}")
    fun getCategoryDetail(
        @PathVariable categoryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CategoryDetailResponse>> {
        return ApiResponse.success(
            GET_CATEGORY_SUCCESS,
            CategoryDetailResponse.of(
                categoryService.getCategoryDetail(categoryId, principalDetails.userId)
            )
        )
    }

    // 내 카테고리 목록 조회
    @GetMapping("/categories/my")
    fun getCategories(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CategoryListResponse>> {
        return ApiResponse.success(
            GET_MY_CATEGORY_LIST_SUCCESS,
            CategoryListResponse.of(
                categoryService.getMyCategoryList(principalDetails.userId)
            )
        )
    }

    // 공개 카테고리 목록 전체 조회
    @GetMapping("/categories/public/all")
    fun getPublicCategories(
        pageable: Pageable,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CategoryListResponse>> {
        return ApiResponse.success(
            GET_PUBLIC_CATEGORY_LIST_SUCCESS,
            CategoryListResponse.of(
                categoryService.getPublicCategoryList(pageable, principalDetails.userId)
            )
        )
    }

    // 오늘의 추천 공개 카테고리 목록 조회
    @GetMapping("/categories/public/today")
    fun getTodayPublicCategories(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<CategoryListResponse>> {
        return ApiResponse.success(
            GET_TODAY_PUBLIC_CATEGORY_LIST_SUCCESS,
            CategoryListResponse.of(
                categoryService.getTodayPublicCategoryList(principalDetails.userId)
            )
        )
    }

    @GetMapping("/categories/like")
    fun getLikeCategories(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LikeCategoryListResponse>> {
        return ApiResponse.success(
            GET_LIKE_CATEGORY_LIST_SUCCESS,
            LikeCategoryListResponse.of(
                categoryLikeService.getLikeCategoryList(principalDetails.userId)
            )
        )
    }

    // ======= Update =======
    // 카테고리 수정
    @PatchMapping("/categories/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: Long,
        @RequestBody @Valid updateCategoryRequest: UpdateCategoryRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<UpdateCategoryResponse>> {
        return ApiResponse.success(
            UPDATE_CATEGORY_SUCCESS,
            UpdateCategoryResponse.of(
                categoryService.updateCategory(categoryId, updateCategoryRequest, principalDetails.userId)
            )
        )
    }

    // ======= Delete =======
    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    fun deleteCategory(
        @PathVariable categoryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<DeleteCategoryResponse>> {
        val category: Category = categoryRepository.findById(categoryId)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)

        if (category.user.id != principalDetails.userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        categoryRepository.delete(category)

        return ApiResponse.success(
            DELETE_CATEGORY_SUCCESS,
            DeleteCategoryResponse.of(categoryId)
        )
    }

    // 좋아요 취소
    @DeleteMapping("/categories/{categoryId}/like")
    fun unlikeCategory(
        @PathVariable categoryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LikeCategoryResponse>> {
        return ApiResponse.success(
            DELETE_CATEGORY_LIKE_SUCCESS,
            LikeCategoryResponse.of(
                categoryLikeService.unlikeCategory(categoryId, principalDetails.userId)
            )
        )
    }

}