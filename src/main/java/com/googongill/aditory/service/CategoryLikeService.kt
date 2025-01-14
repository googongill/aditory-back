package com.googongill.aditory.service

import com.googongill.aditory.common.code.CategoryErrorCode
import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.CategoryLike
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.exception.CategoryException
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.repository.CategoryLikeRepository
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.service.dto.category.LikeCategoryListResult
import com.googongill.aditory.service.dto.category.LikeCategoryResult
//import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.function.Supplier
import java.util.stream.Collectors

@Service
//@Transactional
class CategoryLikeService(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryLikeRepository: CategoryLikeRepository
) {
    fun likeCategory(categoryId: Long?, userId: Long?): LikeCategoryResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        val category: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        if (category.categoryState == CategoryState.PRIVATE) {
            throw CategoryException(CategoryErrorCode.CATEGORY_FORBIDDEN)
        }

        if (categoryLikeRepository.existsByUserAndCategory(user, category)) {
            throw CategoryException(CategoryErrorCode.CATEGORY_ALREADY_LIKED)
        }

        val categoryLike = CategoryLike(user, category)
        categoryLikeRepository.save(categoryLike)
        category.addCategoryLike(categoryLike)

        return LikeCategoryResult.of(category)
    }

    fun unlikeCategory(categoryId: Long?, userId: Long?): LikeCategoryResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        val category: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND)

        if (category.categoryState == CategoryState.PRIVATE) {
            throw CategoryException(CategoryErrorCode.CATEGORY_FORBIDDEN)
        }

        val categoryLike: CategoryLike = categoryLikeRepository.findByUserAndCategory(user, category)
            ?: throw CategoryException(CategoryErrorCode.CATEGORY_NOT_LIKED)

        category.deleteCategoryLike(categoryLike)
        categoryLikeRepository.delete(categoryLike)

        return LikeCategoryResult.of(category)
    }

    fun getLikeCategoryList(userId: Long?): LikeCategoryListResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        val categoryLikeList = categoryLikeRepository.findByUser(user)
        val likeCategoryIdList = categoryLikeList.map { it.category.id }

        return LikeCategoryListResult.of(likeCategoryIdList)
    }
}