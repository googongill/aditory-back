package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.category.UpdateCategoryRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.*;
import com.googongill.aditory.service.dto.link.LinkInfo;

import com.googongill.aditory.controller.dto.category.CreateCategoryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.CategoryErrorCode.FORBIDDEN_CATEGORY;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CreateCategoryResult createCategory(CreateCategoryRequest createCategoryRequest, Long userId) {
        return getCreateCategoryResult(createCategoryRequest, userId);
    }

    private CreateCategoryResult getCreateCategoryResult(CreateCategoryRequest createCategoryRequest, Long userId) {
        //user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        System.out.printf(createCategoryRequest.toEntity().getCategoryName());
        Category createdCategory = categoryRepository.save(createCategoryRequest.toEntity());
        user.addCategory(createdCategory);
        return CreateCategoryResult.of(createdCategory);
    }

    public CategoryListResult getCategoryList(Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 조회한 user 의 카테고리 목록 조회하며 각 카테고리별 정보 입력
        List<CategoryInfo> categoryInfoList = user.getCategories().stream()
                .map(category -> CategoryInfo.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .linkCount(category.getLinks().size())
                        .state(category.getState())
                        .createdAt(category.getCreatedAt())
                        .lastModifiedAt(category.getLastModifiedAt())
                        .build())
                .collect(Collectors.toList());
        return CategoryListResult.of(categoryInfoList);
    }

    public CategoryResult getCategory(Long categoryId, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(FORBIDDEN_CATEGORY);
        }
        // 조회한 category 의 링크 목록 조회하며 각 링크별 정보 입력
        List<LinkInfo> linkInfoList = category.getLinks().stream()
                .map(link -> LinkInfo.builder()
                        .linkId(link.getId())
                        .title(link.getTitle())
                        .summary(link.getSummary())
                        .status(link.getStatus())
                        .createdAt(link.getCreatedAt())
                        .lastModifiedAt(link.getLastModifiedAt())
                        .build()
                ).collect(Collectors.toList());
        return CategoryResult.of(category, linkInfoList);
    }
    public UpdateCategoryResult updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(FORBIDDEN_CATEGORY);
        }
        category.updateCategoryInfo(updateCategoryRequest.getCategoryName(),updateCategoryRequest.getAsCategoryName(),updateCategoryRequest.getState());
        categoryRepository.save(category);
        return UpdateCategoryResult.of(category);

    }


}
