package com.googongill.aditory.service;


import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import com.googongill.aditory.service.dto.category.CategoryResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
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
        // 헤당 user 의 카테고리인지 확인
        if (!category.getUser().getId().equals(userId)) {
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
}
