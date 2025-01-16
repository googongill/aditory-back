package com.googongill.aditory.controller

import com.googongill.aditory.common.ApiResponse
import com.googongill.aditory.common.code.SuccessCode.*
import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.controller.dto.user.request.*
import com.googongill.aditory.controller.dto.user.response.*
import com.googongill.aditory.domain.User
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.external.s3.AWSS3Service
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.security.jwt.user.PrincipalDetails
import com.googongill.aditory.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(
    private val userService: UserService,
    private val awss3Service: AWSS3Service,
    private val userRepository: UserRepository
) {

    // ======= Create =======
    @PostMapping("/users/signup")
    fun signup(
        @RequestBody @Valid signupRequest:SignupRequest
    ): ResponseEntity<ApiResponse<SignupResponse>> {
        return ApiResponse.success(
            SIGNUP_SUCCESS,
            SignupResponse.of(
                userService.createUser(signupRequest)
            )
        )
    }

    @PostMapping("/users/login")
    fun login(
        @RequestBody @Valid loginRequest: LoginRequest
    ): ResponseEntity<ApiResponse<UserTokenResponse>> {
        return ApiResponse.success(
            LOGIN_SUCCESS,
            UserTokenResponse.of(
                userService.loginUser(loginRequest)
            )
        )
    }

    @PostMapping("/oauth/login")
    fun socialLogin(
        @RequestBody @Valid socialLoginRequest: SocialLoginRequest
    ): ResponseEntity<ApiResponse<UserTokenResponse>> {
        return ApiResponse.success(
            LOGIN_SUCCESS,
            UserTokenResponse.of(
                userService.socialLoginUser(socialLoginRequest)
            )
        )
    }

    @PostMapping("/users/logout")
    fun logout(
        @RequestHeader("Authorization") accessToken: String,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<Any>> {
        userService.logoutUser(accessToken, principalDetails.username)

        return ApiResponse.success(LOGOUT_SUCCESS)
    }

    @PostMapping("/users/refresh")
    fun refresh(
        @RequestBody @Valid  refreshRequest:RefreshRequest
    ): ResponseEntity<ApiResponse<UserTokenResponse>> {
        return ApiResponse.success(
            REFRESH_SUCCESS,
            UserTokenResponse.of(
                userService.refreshUser(refreshRequest)
            )
        )
    }

    @PostMapping("/users/profile-image")
    fun updateProfileImage(
        @RequestParam profileImage: MultipartFile,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<ProfileImageResponse>> {
        return ApiResponse.success(
            UPDATE_PROFILE_IMAGE_SUCCESS,
            ProfileImageResponse.of(
                userService.updateProfileImage(profileImage, principalDetails.userId)
            )
        )
    }

    // ======== Read ========

    @GetMapping("/users")
    fun getUserInfo(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<UserInfoResponse>> {
        val user: User = userRepository.findById(principalDetails.userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        return ApiResponse.success(
            GET_USERINFO_SUCCESS,
            UserInfoResponse.of(user)
        )
    }

    @GetMapping("/users/profile-image")
    fun getProfileImage(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<ProfileImageResponse>> {
        val user: User = userRepository.findById(principalDetails.userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        val profileImage = user.fetchProfileImage()
            .orElseThrow { UserException(UserErrorCode.PROFILE_IMAGE_NOT_FOUND) }

        return ApiResponse.success(
            GET_PROFILE_IMAGE_SUCCESS,
            ProfileImageResponse.of(user, awss3Service.downloadOne(profileImage))
        )
    }

    // ======= Update =======
    @PatchMapping("/users")
    fun updateUserInfo(
        @RequestBody @Valid updateUserRequest: UpdateUserRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<UpdateUserResponse>> {
        return ApiResponse.success(
            UPDATE_USER_SUCCESS,
            UpdateUserResponse.of(
                userService.updateUserInfo(updateUserRequest, principalDetails.userId)
            )
        )
    }

    // ======= Delete =======
    @DeleteMapping("/users/signout")
    fun signout(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<ApiResponse<SignoutResponse>> {
        val user: User = userRepository.findById(principalDetails.userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)

        userRepository.delete(user)

        return ApiResponse.success(
            SIGNOUT_SUCCESS,
            SignoutResponse.of(user.id, user.username)
        )
    }
}
