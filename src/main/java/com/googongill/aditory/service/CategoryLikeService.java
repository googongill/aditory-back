package com.googongill.aditory.service;

import com.googongill.aditory.common.code.CategoryErrorCode;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.CategoryLike;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryLikeRepository;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.LikeCategoryResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_FORBIDDEN;
import static com.googongill.aditory.common.code.CategoryErrorCode.CATEGORY_NOT_FOUND;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryLikeService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryLikeRepository categoryLikeRepository;

    public LikeCategoryResult likeCategory(Long categoryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (category.getCategoryState().equals(CategoryState.PRIVATE)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        if (categoryLikeRepository.existsByUserAndCategory(user, category)) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_ALREADY_LIKED);
        }
        CategoryLike categoryLike = new CategoryLike(user, category);
        categoryLikeRepository.save(categoryLike);
        category.addCategoryLike(categoryLike);

        return LikeCategoryResult.of(category);
    }

    public LikeCategoryResult unlikeCategory(Long categoryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (category.getCategoryState().equals(CategoryState.PRIVATE)) {
            throw new CategoryException(CATEGORY_FORBIDDEN);
        }
        CategoryLike categoryLike = categoryLikeRepository.findByUserAndCategory(user, category)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_LIKED));
        category.deleteCategoryLike(categoryLike);
        categoryLikeRepository.delete(categoryLike);

        return LikeCategoryResult.of(category);
    }
}