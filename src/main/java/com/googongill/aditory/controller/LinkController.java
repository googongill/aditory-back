package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.link.*;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.enums.CategoryState;
import com.googongill.aditory.exception.LinkException;
import com.googongill.aditory.repository.LinkRepository;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.LinkErrorCode.LINK_FORBIDDEN;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_FOUND;
import static com.googongill.aditory.common.code.SuccessCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;
    private final LinkRepository linkRepository;

    // ======= Create =======

    @PostMapping("/links")
    public ResponseEntity<ApiResponse<LinkResponse>> createLink(@Valid @RequestBody CreateLinkRequest createLinkRequest,
                                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_LINK_SUCCESS,
                LinkResponse.of(linkService.createLink(createLinkRequest, principalDetails.getUserId())));
    }

    // ======== Read ========

    @GetMapping("/links/{linkId}")
    public ResponseEntity<ApiResponse<LinkDetailResponse>> getLink(@PathVariable Long linkId,
                                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // link 조회
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
        // link 가 소속된 category 의 state 가 private 인데 category 의 소유주가 아닌 user 가 접근하는 경우
        if (link.getCategory().getCategoryState().equals(CategoryState.PRIVATE) &&
            !link.getCategory().getUser().getId().equals(principalDetails.getUserId())) {
            throw new LinkException(LINK_FORBIDDEN);
        }
        // link 읽음 상태 true 로
        link.updateLinkState();
        linkRepository.save(link);
        // link 반환
        return ApiResponse.success(GET_LINK_SUCCESS,
                LinkDetailResponse.of(link));
    }

    @GetMapping("/links/reminder")
    public ResponseEntity<ApiResponse<ReminderResponse>> getReminder(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(GET_REMINDER_SUCCESS,
                ReminderResponse.of(linkService.getReminder(principalDetails.getUserId())));
    }

    // ======= Update =======

    @PatchMapping("/links/{linkId}")
    public ResponseEntity<ApiResponse<LinkResponse>> updateLink(@PathVariable Long linkId, @RequestBody UpdateLinkRequest updateLinkRequest,
                                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_LINK_SUCCESS,
                LinkResponse.of(linkService.updateLink(linkId, updateLinkRequest, principalDetails.getUserId())));
    }

    // ======= Delete =======

    @DeleteMapping("/links/{linkId}")
    public ResponseEntity<ApiResponse<DeleteLinkResponse>> deleteLink(@PathVariable Long linkId,
                                                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
        if (!link.getCategory().getUser().getId().equals(principalDetails.getUserId())) {
            throw new LinkException(LINK_FORBIDDEN);
        }
        Long deletedLinkId = linkId;
        linkRepository.delete(link);
        return ApiResponse.success(DELETE_LINK_SUCCESS,
                DeleteLinkResponse.of(deletedLinkId));
    }

}
