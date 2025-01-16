package com.googongill.aditory.controller

import com.googongill.aditory.common.ApiResponse
import com.googongill.aditory.common.code.LinkErrorCode.*
import com.googongill.aditory.common.code.SuccessCode.*
import com.googongill.aditory.controller.dto.link.request.CreateLinkRequest
import com.googongill.aditory.controller.dto.link.request.UpdateLinkRequest
import com.googongill.aditory.controller.dto.link.response.DeleteLinkResponse
import com.googongill.aditory.controller.dto.link.response.LinkDetailResponse
import com.googongill.aditory.controller.dto.link.response.LinkListResponse
import com.googongill.aditory.controller.dto.link.response.LinkResponse
import com.googongill.aditory.domain.enums.CategoryState
import com.googongill.aditory.exception.LinkException
import com.googongill.aditory.repository.LinkRepository
import com.googongill.aditory.security.jwt.user.PrincipalDetails
import com.googongill.aditory.service.LinkService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class LinkController(
    private val linkService: LinkService,
    private val linkRepository: LinkRepository
) {

    // ======= Create =======
    @PostMapping("/links")
    fun createLink(
        @RequestBody @Valid createLinkRequest: CreateLinkRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LinkResponse>> {
        return ApiResponse.success(
            SAVE_LINK_SUCCESS,
            LinkResponse.of(linkService.createLink(createLinkRequest, principalDetails.userId))
        )
    }

    // ======== Read ========
    @GetMapping("/links/{linkId}")
    fun getLink(
        @PathVariable linkId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LinkDetailResponse>> {
        // link 조회
        val link = linkRepository.findById(linkId).orElseThrow { LinkException(LINK_NOT_FOUND) }!!
        // link 가 소속된 category 의 state 가 private 인데 category 의 소유주가 아닌 user 가 접근하는 경우
        if (link.category.categoryState == CategoryState.PRIVATE && link.user.id != principalDetails.userId) {
            throw LinkException(LINK_FORBIDDEN)
        }
        // link 읽음 상태 true 로
        link.updateLinkState()
        linkRepository.save(link)

        // link 반환
        return ApiResponse.success(
            GET_LINK_SUCCESS,
            LinkDetailResponse.of(link)
        )
    }

    @GetMapping("/links/reminder")
    fun getReminder(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LinkListResponse>> {
        return ApiResponse.success(
            GET_REMINDER_SUCCESS,
            LinkListResponse.of(linkService.getReminder(principalDetails.userId))
        )
    }

    // ======= Update =======
    @PatchMapping("/links/{linkId}")
    fun updateLink(
        @PathVariable linkId: Long,
        @RequestBody @Valid updateLinkRequest: UpdateLinkRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<LinkResponse>> {
        return ApiResponse.success(
            UPDATE_LINK_SUCCESS,
            LinkResponse.of(linkService.updateLink(linkId, updateLinkRequest, principalDetails.userId))
        )
    }

    // ======= Delete =======
    @DeleteMapping("/links/{linkId}")
    fun deleteLink(
        @PathVariable linkId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<DeleteLinkResponse>> {
        val link = linkRepository.findById(linkId).orElseThrow { LinkException(LINK_NOT_FOUND) }!!

        if (link.user.id != principalDetails.userId) { throw LinkException(LINK_FORBIDDEN) }
        linkRepository.delete(link)

        return ApiResponse.success(
            DELETE_LINK_SUCCESS,
            DeleteLinkResponse.of(linkId)
        )
    }

}