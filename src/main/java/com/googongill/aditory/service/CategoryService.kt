package com.googongill.aditory.service

import com.googongill.aditory.common.code.CategoryErrorCode.*
import com.googongill.aditory.common.code.LinkErrorCode.*
import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.controller.dto.category.request.CreateCategoryRequest
import com.googongill.aditory.controller.dto.category.request.MoveCategoryRequest
import com.googongill.aditory.controller.dto.category.request.UpdateCategoryRequest
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.Link
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.exception.CategoryException
import com.googongill.aditory.exception.LinkException
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.repository.LinkRepository
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.service.dto.category.*
import com.googongill.aditory.service.dto.link.LinkInfo
import jakarta.transaction.Transactional
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.stream.Collectors

@Service
//@Transactional
class CategoryService(
    private val userRepository: UserRepository,
    private val linkRepository: LinkRepository,
    private val categoryRepository: CategoryRepository
) {
    fun createCategory(createCategoryRequest: CreateCategoryRequest, userId: Long?): CreateCategoryResult {
        // user 조회
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        // category 갯수 제한
        if (user.categories.size >= 30) {
            throw CategoryException(CATEGORY_LIMIT_EXCEEDED)
        }
        if (categoryRepository.findByCategoryNameAndUser(createCategoryRequest.categoryName, user) != null) {
            throw CategoryException(CATEGORY_ALREADY_EXISTED)
        }
        // category 생성
        val createdCategory = categoryRepository.save(createCategoryRequest.toEntity(user))
        user.addCategory(createdCategory)

        return CreateCategoryResult.of(createdCategory)
    }

    //카테고리 복사
    fun copyCategory(categoryId: Long?, userId: Long?): CopyCategoryResult {
        // 카테고리 조회
        val category: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.categoryState == CategoryState.PRIVATE && category.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        if (user.categories.any { it.id == categoryId }) {
            throw CategoryException(CATEGORY_ALREADY_OWNED)
        }
        // category 갯수 제한
        if (user.categories.size >= 30) {
            throw CategoryException(CATEGORY_LIMIT_EXCEEDED)
        }
        if (categoryRepository.findByCategoryNameAndUser(category.asCategoryName, user) != null) {
            throw CategoryException(CATEGORY_ALREADY_EXISTED)
        }
        // 새카테고리 생성 및 user 설정
        val newCategory = Category(category.asCategoryName, category.asCategoryName, user)
        // 원본 카테고리의 링크를 가져옴
        val originalLinks: List<Link> = category.links
        // 링크를 복사하여 새로운 카테고리에 추가
        val newLinks = originalLinks.map { link ->
            Link(link.title, link.summary, link.url, newCategory, user)
        }
        newLinks.forEach { linkRepository.save(it) }
        newCategory.links.addAll(newLinks)
        // 새카테고리를 사용자의 카테고리 목록에 추가
        user.categories.add(newCategory)
        // 새 카테고리 저장
        categoryRepository.save(newCategory)

        return CopyCategoryResult.of(newCategory)
    }

    // 카테고리 속 링크 이동
    fun moveCategory(categoryId: Long?, moveCategoryRequest: MoveCategoryRequest, userId: Long?): CategoryDetailResult {
        // user 조회
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        // 원래 카테고리 조회
        val originalCategory: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // 원래 카테고리의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (originalCategory.categoryState == CategoryState.PRIVATE && originalCategory.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        // 대상 카테고리 조회
        val targetCategory: Category = categoryRepository.findById(moveCategoryRequest.targetCategoryId)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // 대상 카테고리의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (targetCategory.categoryState == CategoryState.PRIVATE && targetCategory.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }

        moveCategoryRequest.linkIdList.forEach { linkId ->
            val link = linkRepository.findById(linkId).orElseThrow { LinkException(LINK_NOT_FOUND) }
            if (link!!.category.id != originalCategory.id) {
                throw LinkException(LINK_NOT_IN_CATEGORY)
            }
            link.category = targetCategory
            targetCategory.links.add(link)
            linkRepository.save(link)
        }

        // 대상 카테고리의 링크 목록 조회하며 각 링크별 정보 입력
        val linkInfoList = targetCategory.links.map { link ->
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

        return CategoryDetailResult.of(targetCategory, linkInfoList)
    }

    fun getCategoryDetail(categoryId: Long?, userId: Long?): CategoryDetailResult {
        // 카테고리 조회
        val category: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.categoryState == CategoryState.PRIVATE && category.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        // 조회한 category 의 링크 목록 조회하며 각 링크별 정보 입력
        val linkInfoList = category.links.map { link: Link ->
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

        return CategoryDetailResult.of(category, linkInfoList)
    }

    fun getMyCategoryList(userId: Long?): CategoryListResult {
        // user 조회
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        // 조회한 user 의 카테고리 목록 조회하며 각 카테고리별 정보 입력
        val myCategoryInfoList = user.categories.map { category: Category ->
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
                    .map{ it.url },
                createdAt = category.createdAt,
                lastModifiedAt = category.lastModifiedAt
            )
        }

        return CategoryListResult.of(myCategoryInfoList)
    }

    fun getPublicCategoryList(pageable: Pageable, userId: Long?): Page<CategoryInfo> {
        // user 조회
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        // 모든 사용자 카테고리 중에서 state가 public 인 것만 조회
        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by("lastModifiedAt").descending())
        val categories = categoryRepository.findAllByCategoryState(CategoryState.PUBLIC, pageRequest)
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

    fun getTodayPublicCategoryList(userId: Long?): CategoryListResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        val categoryInfos = categoryRepository.findRandomByCategoryState(CategoryState.PUBLIC.toString()).map { category: Category ->
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
                    lastModifiedAt= category.lastModifiedAt
                )
            }

        return CategoryListResult.of(categoryInfos)
    }

    fun updateCategory(
        categoryId: Long?,
        updateCategoryRequest: UpdateCategoryRequest,
        userId: Long
    ): UpdateCategoryResult {
        // 카테고리 조회
        val category: Category = categoryRepository.findById(categoryId!!)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // category 의 state 가 private 인데 카테고리의 소유주가 아닌 user 가 접근하는 경우
        if (category.categoryState == CategoryState.PRIVATE && category.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        category.updateCategoryInfo(
            updateCategoryRequest.categoryName,
            updateCategoryRequest.asCategoryName,
            updateCategoryRequest.categoryState
        )
        categoryRepository.save(category)

        return UpdateCategoryResult.of(category)
    }

    fun importCategories(importFile: MultipartFile, userId: Long?): List<Category> {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        val newCategories: MutableList<Category> = ArrayList()
        try {
            val doc = Jsoup.parse(importFile.inputStream, "UTF-8", "")
            val unReadElements = doc.select("h1:contains(Unread) + ul > li > a")
            processElements(unReadElements, user, true, newCategories)
            val readElements = doc.select("h1:contains(Read Archive) + ul > li > a")
            processElements(readElements, user, true, newCategories)
        } catch (e: IOException) {
            throw CategoryException(IMPORT_FILE_PARSE_FAIL)
        }

        return newCategories
    }

    private fun processElements(elements: Elements, user: User, linkState: Boolean, newCategories: MutableList<Category>) {
        elements.map { element: Element ->
                val categoryName = element.attr("tags")
                val linkTitle = element.text()
                val url = element.attr("href")

                if (categoryName.isNotEmpty()) {
                    addLinkToAlreadyExistingCategory(user, linkState, categoryName, linkTitle, url, newCategories)
                } else {
                    addLinkAndCategory(user, linkState, linkTitle, url, newCategories)
                }
            }
    }

    private fun addLinkToAlreadyExistingCategory(
        user: User,
        linkState: Boolean,
        categoryName: String,
        linkTitle: String,
        url: String,
        newCategories: MutableList<Category>
    ) {
        val category: Category = categoryRepository.findByCategoryNameAndUser(categoryName, user)
            ?: run {
                val newCategory = categoryRepository.save(Category(categoryName, categoryName, user))
                user.addCategory(newCategory)
                newCategories.add(newCategory)
                newCategory
            }
        val link = Link(linkTitle, url, linkState, category, user)
        linkRepository.save(link)
        category.addLink(link)
        user.addLink(link)
    }

    private fun addLinkAndCategory(user: User, linkState: Boolean, linkTitle: String, url: String, newCategories: MutableList<Category>) {
        val category: Category = categoryRepository.findByCategoryName("imported Category")
            ?: run {
                val newCategory = categoryRepository.save(Category("imported Category", "imported Category", user))
                user.addCategory(newCategory)
                newCategories.add(newCategory)
                newCategory
            }
        val link = Link(linkTitle, url, linkState, category, user)
        linkRepository.save(link)
        category.addLink(link)
        user.addLink(link)
    }
}