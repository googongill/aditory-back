package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.CreateLinkResponse;
import com.googongill.aditory.controller.dto.link.LinkResponse;
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

import static com.googongill.aditory.common.code.LinkErrorCode.FORBIDDEN_LINK;
import static com.googongill.aditory.common.code.LinkErrorCode.LINK_NOT_FOUND;
import static com.googongill.aditory.common.code.SuccessCode.GET_LINK_SUCCESS;
import static com.googongill.aditory.common.code.SuccessCode.SAVE_LINK_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;
    private final LinkRepository linkRepository;

    // ======= Create =======

    @PostMapping("/links")
    public ResponseEntity<ApiResponse<CreateLinkResponse>> createLink(@Valid @ModelAttribute CreateLinkRequest createLinkRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_LINK_SUCCESS,
                CreateLinkResponse.of(linkService.createLink(createLinkRequest, principalDetails.getUserId())));
    }

    // ======== Read ========

    @GetMapping("/links/{linkId}")
    public ResponseEntity<ApiResponse<LinkResponse>> getLink(@PathVariable Long linkId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // link 조회
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new LinkException(LINK_NOT_FOUND));
        // link 가 소속된 category 의 state 가 private 인데 category 의 소유주가 아닌 user 가 접근하는 경우
        if (link.getCategory().getState().equals(CategoryState.PRIVATE) &&
            !link.getCategory().getUser().getId().equals(principalDetails.getUserId())) {
            throw new LinkException(FORBIDDEN_LINK);
        }
        // link 반환
        return ApiResponse.success(GET_LINK_SUCCESS,
                LinkResponse.of(link));
    }

    // ======= Update =======


    // ======= Delete =======


}
