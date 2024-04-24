package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.user.*;
import com.googongill.aditory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.SuccessCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ======= Create =======

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignResponse>> signup(@Valid @ModelAttribute SignupRequest signupRequest) {
        return ApiResponse.success(SIGNUP_SUCCESS,
                SignResponse.of(userService.createUser(signupRequest)));
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse<UserTokenResponse>> login(@Valid @ModelAttribute LoginRequest loginRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
                UserTokenResponse.of(userService.loginUser(loginRequest)));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal UserDetails userDetails) {
        userService.logoutUser(userDetails.getUsername(), accessToken);
        return ApiResponse.success(LOGOUT_SUCCESS);
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<ApiResponse<UserTokenResponse>> refresh(@Valid @ModelAttribute RefreshRequest refreshRequest) {
        return ApiResponse.success(REFRESH_SUCCESS,
                UserTokenResponse.of(userService.refreshUser(refreshRequest)));
    }

    // ======== Read ========

    @GetMapping("/users/test")
    public String test() {
        return "test success!";
    }

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
