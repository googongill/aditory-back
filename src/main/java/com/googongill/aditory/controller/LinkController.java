package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.common.code.SuccessCode;
import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.CreateLinkResponse;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.googongill.aditory.common.code.SuccessCode.SAVE_LINK_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    // ======= Create =======

    @PostMapping("/links")
    public ResponseEntity<ApiResponse<CreateLinkResponse>> createLink(@Valid @ModelAttribute CreateLinkRequest createLinkRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(SAVE_LINK_SUCCESS,
                CreateLinkResponse.of(linkService.createLink(createLinkRequest, principalDetails.getUser())));
    }

    // ======== Read ========


    // ======= Update =======


    // ======= Delete =======


}
