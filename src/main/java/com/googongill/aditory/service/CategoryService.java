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
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_FORBIDDEN;
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
        //category 생성
        Category createdCategory = categoryRepository.save(createCategoryRequest.toEntity(user));
        user.addCategory(createdCategory);
        return CreateCategoryResult.of(createdCategory);
    }

    public CategoryListResult getCategoryList(Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 조회한 user 의 카테고리 목록 조회하며 각 카테고리별 정보 입력
        List<MyCategoryInfo> myCategoryInfoList = user.getCategories().stream()
                .map(category -> MyCategoryInfo.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .linkCount(category.getLinks().size())
                        .categoryState(category.getCategoryState())
                        .createdAt(category.getCreatedAt())
                        .lastModifiedAt(category.getLastModifiedAt())
                        .build())
                .collect(Collectors.toList());
        return CategoryListResult.of(myCategoryInfoList);
    }
    public PublicCategoryListResult getPublicCategoryList(Long userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 모든 사용자 카테고리 중에서 state가 public 인 것만 조회
        List<PublicCategoryInfo> publicCategoryInfoList = categoryRepository.findAllByCategoryState(CategoryState.PUBLIC).stream()
                .map(category -> PublicCategoryInfo.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .asCategoryName(category.getAsCategoryName())
                            .linkCount(category.getLinks().size())
                            .categoryState(category.getCategoryState())
                            .createdAt(category.getCreatedAt())
                            .lastModifiedAt(category.getLastModifiedAt())
                            .build())
                .collect(Collectors.toList());
        return PublicCategoryListResult.of(publicCategoryInfoList);
    }

    public MyCategoryResult getCategory(Long categoryId, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        // 조회한 category 의 링크 목록 조회하며 각 링크별 정보 입력
        List<LinkInfo> linkInfoList = category.getLinks().stream()
                .map(link -> LinkInfo.builder()
                        .linkId(link.getId())
                        .title(link.getTitle())
                        .summary(link.getSummary())
                        .linkState(link.getLinkState())
                        .createdAt(link.getCreatedAt())
                        .lastModifiedAt(link.getLastModifiedAt())
                        .build()
                ).collect(Collectors.toList());
        return MyCategoryResult.of(category, linkInfoList);
    }
    public UpdateCategoryResult updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest, Long userId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.getCategoryState().equals(CategoryState.PRIVATE) && !category.getUser().getId().equals(userId)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        category.updateCategoryInfo(updateCategoryRequest.getCategoryName(),updateCategoryRequest.getAsCategoryName(),updateCategoryRequest.getCategoryState());
        categoryRepository.save(category);
        return UpdateCategoryResult.of(category);
    }
}
