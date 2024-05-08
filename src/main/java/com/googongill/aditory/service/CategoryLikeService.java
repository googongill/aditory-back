// CategoryLikeService.java
package com.googongill.aditory.service;

import com.googongill.aditory.common.code.CategoryErrorCode;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.CategoryLike;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.CategoryException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryLikeRepository;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.category.LikeCategoryResult;
import com.googongill.aditory.service.dto.category.UnlikeCategoryResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        if (categoryLikeRepository.existsByUserAndCategory(user, category)) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_ALREADY_LIKED);
        }
        CategoryLike categoryLike = new CategoryLike(user, category);
        categoryLikeRepository.save(categoryLike);
        Long likeCount = categoryLikeRepository.countByCategory(category);
        LikeCategoryResult likeCategoryResult = LikeCategoryResult.builder()
                .categoryId(categoryId)
                .likeCount(likeCount)
                .build();

        return likeCategoryResult.of(categoryId, likeCount);
    }

    public UnlikeCategoryResult unlikeCategory(Long categoryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        CategoryLike categoryLike = categoryLikeRepository.findByUserAndCategory(user, category)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_LIKED));

        categoryLikeRepository.delete(categoryLike);
        Long likeCount = categoryLikeRepository.countByCategory(category);
        UnlikeCategoryResult unlikeCategoryResult = UnlikeCategoryResult.builder()
                .categoryId(categoryId)
                .likeCount(likeCount)
                .build();

        return unlikeCategoryResult.of(categoryId, likeCount);
    }
}