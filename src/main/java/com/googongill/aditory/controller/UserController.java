package com.googongill.aditory.controller;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.controller.dto.user.request.*;
import com.googongill.aditory.controller.dto.user.response.*;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.external.s3.AWSS3Service;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.googongill.aditory.common.code.SuccessCode.*;
import static com.googongill.aditory.common.code.UserErrorCode.PROFILE_IMAGE_NOT_FOUND;
import static com.googongill.aditory.common.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AWSS3Service awss3Service;
    private final UserRepository userRepository;

    // ======= Create =======

    @PostMapping("/users/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.success(SIGNUP_SUCCESS,
                SignupResponse.of(userService.createUser(signupRequest)));
    }

    @PostMapping("/users/login")
        public ResponseEntity<ApiResponse<UserTokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
                UserTokenResponse.of(userService.loginUser(loginRequest)));
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponse<UserTokenResponse>> socialLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
        return ApiResponse.success(LOGIN_SUCCESS,
                UserTokenResponse.of(userService.socialLoginUser(socialLoginRequest)));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String accessToken,
                                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.logoutUser(accessToken, principalDetails.getUsername());
        return ApiResponse.success(LOGOUT_SUCCESS);
    }

    @PostMapping("/users/refresh")
    public ResponseEntity<ApiResponse<UserTokenResponse>> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ApiResponse.success(REFRESH_SUCCESS,
                UserTokenResponse.of(userService.refreshUser(refreshRequest)));
    }

    @PostMapping("/users/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> updateProfileImage(@RequestParam MultipartFile profileImage,
                                                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ApiResponse.success(UPDATE_PROFILE_IMAGE_SUCCESS,
                ProfileImageResponse.of(userService.updateProfileImage(profileImage, principalDetails.getUserId())));
    }

    // ======== Read ========

    @GetMapping("/home")
    public String welcome() {
        return "Welcome";
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUserId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        return ApiResponse.success(GET_USERINFO_SUCCESS,
                UserInfoResponse.of(user));
    }

    @GetMapping("/users/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> getProfileImage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUserId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        ProfileImage profileImage = user.getProfileImage()
                .orElseThrow(() -> new UserException(PROFILE_IMAGE_NOT_FOUND));
        return ApiResponse.success(GET_PROFILE_IMAGE_SUCCESS,
                ProfileImageResponse.of(user, awss3Service.downloadOne(profileImage)));
    }

    // ======= Update =======

    @PatchMapping("/users")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUserInfo(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
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
