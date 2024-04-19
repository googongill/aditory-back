package com.googongill.aditory.controller;

import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.controller.dto.user.SignupResponse;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Role role = Role.valueOf("ROLE_USER");

    // ======= Create =======
    @PostMapping("/user/signup")
    public ResponseEntity<SignupResponse> signup(@ModelAttribute SignupRequest signupRequest) {
        return null;
    }

    // ======== Read ========


    // ======= Update =======


    // ======= Delete =======


}
