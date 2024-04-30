package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.user.*;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.googongill.aditory.common.code.SuccessCode.*;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // ======= Create =======

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.success(SIGNUP_SUCCESS,
                SignResponse.of(userService.createUser(signupRequest)));
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse<UserTokenResponse>> login(@Valid @ModelAttribute LoginRequest loginRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
                UserTokenResponse.of(userService.loginUser(loginRequest)));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken,
                                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.logoutUser(principalDetails.getUsername(), accessToken);
        return ApiResponse.success(LOGOUT_SUCCESS);
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<ApiResponse<UserTokenResponse>> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
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
    public ResponseEntity<ApiResponse<UpdateUserResponse>> update(@Valid @RequestBody UpdateUserRequest updateUserRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_USER_SUCCESS,
                UpdateUserResponse.of(userService.updateUserInfo(updateUserRequest, principalDetails.getUserId())));
    }

    // ======= Delete =======

    @DeleteMapping("/users/signout")
    public ResponseEntity<ApiResponse<SignoutResponse>> signout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUserId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        SignoutResponse signoutResponse = SignoutResponse.of(user.getId(), user.getUsername());
        userRepository.delete(user);
        return ApiResponse.success(SIGNOUT_SUCCESS, signoutResponse);
    }
}
