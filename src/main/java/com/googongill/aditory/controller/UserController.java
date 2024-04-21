package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.controller.dto.user.SignupResponse;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.service.UserService;
import com.googongill.aditory.service.dto.SignupResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.SuccessCode.SIGNUP_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Role role = Role.valueOf("ROLE_USER");

    // ======= Create =======

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @ModelAttribute SignupRequest signupRequest) {
        SignupResult result = userService.createUser(signupRequest.getUsername(), signupRequest.getPassword(), role, signupRequest.getNickname(), signupRequest.getContact());
        return ApiResponse.success(SIGNUP_SUCCESS,
                SignupResponse.of(result));
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse> login() {
        return null;
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse> logout() {
        return null;
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<ApiResponse> refresh() {
        return null;
    }

    // ======== Read ========


    // ======= Update =======

    @PatchMapping("/users")
    public ResponseEntity<ApiResponse> updateUser() {
        return null;
    }

    // ======= Delete =======

    @DeleteMapping("/users/signout")
    public ResponseEntity<ApiResponse> signout() {
        return null;
    }
}
