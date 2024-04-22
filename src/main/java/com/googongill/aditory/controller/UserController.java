package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.LoginResponse;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.controller.dto.user.SignupResponse;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.service.UserService;
import com.googongill.aditory.service.dto.user.LoginResult;
import com.googongill.aditory.service.dto.user.SignupResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.SuccessCode.*;
import static com.googongill.aditory.common.code.UserErrorCode.TOKEN_INVALID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Role role = Role.valueOf("ROLE_USER");

    // ======= Create =======

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @ModelAttribute SignupRequest signupRequest) {
        return ApiResponse.success(SIGNUP_SUCCESS,
                SignupResponse.of(userService.createUser(
                        signupRequest.getUsername(),
                        signupRequest.getPassword(),
                        role,
                        signupRequest.getNickname(),
                        signupRequest.getContact())
                )
        );
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @ModelAttribute LoginRequest loginRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
                LoginResponse.of(userService.login(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
                )
        );
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal UserDetails userDetails) {
        userService.logout(userDetails.getUsername(), accessToken);
        return ApiResponse.success(LOGOUT_SUCCESS);
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
