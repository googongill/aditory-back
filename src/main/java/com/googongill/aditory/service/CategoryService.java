package com.googongill.aditory.service;


import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.CategoryInfo;
import com.googongill.aditory.service.dto.category.CategoryListResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;

    public CategoryListResult getCategoryList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
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
}
