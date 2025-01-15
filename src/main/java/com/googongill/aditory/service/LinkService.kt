package com.googongill.aditory.service

import com.googongill.aditory.common.code.CategoryErrorCode
import com.googongill.aditory.common.code.CategoryErrorCode.*
import com.googongill.aditory.common.code.LinkErrorCode
import com.googongill.aditory.common.code.LinkErrorCode.*
import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.common.code.UserErrorCode.*
import com.googongill.aditory.controller.dto.link.request.CreateLinkRequest
import com.googongill.aditory.controller.dto.link.request.UpdateLinkRequest
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.Link
import com.googongill.aditory.domain.User
import com.googongill.aditory.exception.CategoryException
import com.googongill.aditory.exception.LinkException
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.external.chatgpt.ChatGptService
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.repository.LinkRepository
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.service.dto.link.LinkInfo
import com.googongill.aditory.service.dto.link.LinkListResult
import com.googongill.aditory.service.dto.link.LinkResult
//import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
//@Transactional
class LinkService(
    private val userRepository: UserRepository,
    private val linkRepository: LinkRepository,
    private val chatGptService: ChatGptService,
    private val categoryRepository: CategoryRepository
) {
    fun createLink(createLinkRequest: CreateLinkRequest, userId: Long): LinkResult {
        return if (createLinkRequest.autoComplete) {
            getAutoCreateLinkResult(createLinkRequest, userId)
        } else {
            getCreateLinkResult(createLinkRequest, userId)
        }
    }

    private fun getAutoCreateLinkResult(createLinkRequest: CreateLinkRequest, userId: Long): LinkResult {
        // 사용자 카테고리 이름 목록 조회
        val user: User = userRepository.findById(userId)

            ?: throw UserException(USER_NOT_FOUND)
        val userCategoryNameList = user.categories.map { it.categoryName }

        // chat-gpt 에 url, 카테고리 목록으로 자동 요약 및 분류 결과 조회
        val autoCategorizeResult = chatGptService.autoCategorizeLink(createLinkRequest.url, userCategoryNameList)
        // 해당 카테고리 조회
        val category: Category = categoryRepository.findByCategoryNameAndUser(autoCategorizeResult.categoryName, user)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        // 링크 생성
        val createdLink = linkRepository.save(createLinkRequest.toEntity(autoCategorizeResult, category, user))
        // 링크 추가 (연관관계 메서드)
        category.addLink(createdLink)
        user.addLink(createdLink)

        // 링크 생성 결과
        return LinkResult.of(createdLink, category)
    }

    private fun getCreateLinkResult(createLinkRequest: CreateLinkRequest, userId: Long): LinkResult {
        // 사용자 조회
        val user: User = userRepository.findById(userId)
            ?: throw UserException(USER_NOT_FOUND)
        // 카테고리 조회
        val category: Category = categoryRepository.findById(createLinkRequest.categoryId!!)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        if (category.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        // 링크 생성
        val createdLink = linkRepository.save(createLinkRequest.toEntity(category, user))
        // 링크 추가 (연관관계 메서드)
        category.addLink(createdLink)
        user.addLink(createdLink)

        // 링크 생성 결과
        return LinkResult.of(createdLink, category)
    }

    fun updateLink(linkId: Long, updateLinkRequest: UpdateLinkRequest, userId: Long): LinkResult {
        // 링크 조회
        val link = linkRepository.findById(linkId)
            .orElseThrow { LinkException(LINK_NOT_FOUND) }!!
        if (link.user.id != userId) {
            throw LinkException(LINK_FORBIDDEN)
        }
        // 카테고리 조회
        val category: Category = categoryRepository.findById(updateLinkRequest.categoryId)
            ?: throw CategoryException(CATEGORY_NOT_FOUND)
        if (category.user.id != userId) {
            throw CategoryException(CATEGORY_FORBIDDEN)
        }
        // 링크 정보 수정 (연관관계 메서드)
        link.updateLinkInfo(updateLinkRequest.title, updateLinkRequest.summary, updateLinkRequest.url, category)
        linkRepository.save(link)

        return LinkResult.of(link, category)
    }

    fun getReminder(userId: Long?): LinkListResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(USER_NOT_FOUND)

        val oldestLinks = linkRepository.findTop10ByUserAndLinkStateOrderByCreatedAtAsc(user, false)
        if (oldestLinks.isEmpty()) {
            throw LinkException(REMINDER_EMPTY)
        }
        val linkInfoList = oldestLinks.map{ link: Link ->
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

        return LinkListResult.of(linkInfoList)
    }
}
