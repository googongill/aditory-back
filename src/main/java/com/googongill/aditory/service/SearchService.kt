package com.googongill.aditory.service

import com.googongill.aditory.common.code.SearchErrorCode
import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.controller.dto.search.SearchRequest
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.Link
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.CategoryScope
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.exception.SearchException
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.repository.LinkRepository
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.service.dto.category.CategoryInfo
import com.googongill.aditory.service.dto.link.LinkInfo
//import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
//@Transactional
class SearchService(
    private val userRepository: UserRepository,
    private val linkRepository: LinkRepository,
    private val categoryRepository: CategoryRepository
) {

    fun searchCategories(searchRequest: SearchRequest, pageable: Pageable, userId: Long?): Page<CategoryInfo> {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        val query = searchRequest.query

        return when (searchRequest.categoryScope) {
            CategoryScope.IN_MY -> searchByCategoryName(query, pageable, user, null)
            CategoryScope.IN_PUBLIC -> searchByCategoryName(query, pageable, null, CategoryState.PUBLIC)
            else -> throw SearchException(SearchErrorCode.INVALID_SEARCH_TYPE)
        }
    }

    private fun searchByCategoryName(query: String, pageable: Pageable, user: User?, categoryState: CategoryState?): Page<CategoryInfo> {
        val categories: Page<Category> = if (user != null) {
            categoryRepository.findByCategoryNameContainingAndUser(query, user, pageable)
        } else {
            categoryRepository.findByAsCategoryNameContainingAndCategoryState(query, categoryState!!, pageable)
        }

        return categories.map { category: Category ->
            CategoryInfo(
                categoryId = category.id,
                categoryName = category.categoryName,
                asCategoryName = category.asCategoryName,
                linkCount = category.links.size,
                likeCount = category.categoryLikes.size,
                categoryState = category.categoryState,
                prevLinks = category.links
                        .sortedByDescending { it.createdAt }
                        .take(4)
                        .map { it.url },
                createdAt = category.createdAt,
                lastModifiedAt = category.lastModifiedAt
            )
        }
    }

    fun searchLinks(searchRequest: SearchRequest, pageable: Pageable, userId: Long?): Page<LinkInfo> {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        val query = searchRequest.query
        val categoryScope = searchRequest.categoryScope
        return if (categoryScope == CategoryScope.IN_MY) {
            searchByLinkTitle(query, pageable, user, null)
        } else if (categoryScope == CategoryScope.IN_PUBLIC) {
            searchByLinkTitle(query, pageable, null, CategoryState.PUBLIC)
        } else {
            throw SearchException(SearchErrorCode.INVALID_CATEGORY_SCOPE)
        }
    }

    private fun searchByLinkTitle(query: String, pageable: Pageable, user: User?, categoryState: CategoryState?): Page<LinkInfo> {
        val links: Page<Link>
        links = if (user != null) {
            linkRepository.findByTitleContainingAndUser(query, user, pageable)
        } else {
            linkRepository.findByTitleContainingAndCategory_CategoryState(query, categoryState!!, pageable)
        }
        return links.map { link: Link ->
            LinkInfo(
                linkId = link.id,
                title = link.title,
                summary = link.summary,
                url = link.url,
                linkState = link.linkState,
                createdAt = link.createdAt,
                lastModifiedAt = link.lastModifiedAt
            )
        }
    }

}