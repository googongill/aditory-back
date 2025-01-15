package com.googongill.aditory.service

import com.googongill.aditory.common.code.UserErrorCode.*
import com.googongill.aditory.controller.dto.user.request.*
import com.googongill.aditory.domain.Category
import com.googongill.aditory.domain.ProfileImage
import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.Role
import com.googongill.aditory.domain.enums.SocialType
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.external.s3.AWSS3Service
import com.googongill.aditory.repository.CategoryRepository
import com.googongill.aditory.repository.ProfileImageRepository
import com.googongill.aditory.repository.UserRepository
import com.googongill.aditory.security.jwt.TokenProvider
import com.googongill.aditory.security.jwt.TokenProvider.validateToken
import com.googongill.aditory.security.oauth.KakaoUserProfile
import com.googongill.aditory.security.oauth.OAuth2UserInfo
import com.googongill.aditory.service.dto.user.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
//@Transactional
class UserService(
    private val awss3Service: AWSS3Service,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val profileImageRepository: ProfileImageRepository,
    private val inMemoryRepository: InMemoryClientRegistrationRepository
) {

    fun createUser(signupRequest: SignupRequest): SignupResult {
        // 이미 존재하는 username 존재하는지 확인
        if (userRepository.findByUsername(signupRequest.username) != null) {
            throw UserException(ALREADY_EXISTING_USERNAME)
        }
        // 사용자 생성
        val createdUser = signupRequest.toEntity()
        userRepository.save(createdUser)
        // 카테고리 생성
        val createdCategories = signupRequest.userCategories.map { categoryName: String ->
                val category = Category(categoryName, categoryName, createdUser)
                categoryRepository.save(category)
        }

        // 카테고리 추가 (연관관계 메서드)
        createdUser.addCategories(createdCategories)
        return SignupResult.of(createdUser, createdCategories)
    }

    fun loginUser(loginRequest: LoginRequest): UserTokenResult {
        // username 확인
        val user: User = userRepository.findByUsername(loginRequest.username)
            ?: throw UserException(USER_NOT_FOUND)
        // 비밀번호 일치 확인
        if (!bCryptPasswordEncoder.matches(loginRequest.password, user.password)) {
            throw UserException(PASSWORD_INVALID)
        }
        // 토큰 발급
        val jwtResult = TokenProvider.createTokens(user.id, user.username, user.role)
        // User 에 refresh Token 저장
        val refreshToken = jwtResult.refreshToken
        user.saveRefreshToken(refreshToken)
        userRepository.save(user)
        // 로그인 완료
        return UserTokenResult.of(user, jwtResult)
    }

    fun logoutUser(accessToken: String?, username: String?) {
        // username 확인
        val user: User = userRepository.findByUsername(username)
            ?: throw UserException(USER_NOT_FOUND)
        // refreshToken 삭제
        user.deleteRefreshToken()
        userRepository.save(user)
    }

    fun refreshUser(refreshRequest: RefreshRequest): UserTokenResult {
        // request refreshToken 검증
        val requestRefreshToken = getRequestRefreshToken(refreshRequest)
        // userId 확인
        val user: User = userRepository.findById(refreshRequest.userId)
            ?: throw UserException(USER_NOT_FOUND)
        // db 의 refreshToken
        val dbRefreshToken = getDbRefreshToken(user)
        // db 와 request 토큰 일치 확인
        if (requestRefreshToken != dbRefreshToken) {
            throw UserException(TOKEN_INVALID)
        }

        val newToken = TokenProvider.createTokens(user.id, user.username, user.role)
        // User 에 refresh Token 저장
        val refreshToken = newToken.refreshToken
        user.saveRefreshToken(refreshToken)
        userRepository.save(user)

        return UserTokenResult.of(user, newToken)
    }

    fun updateProfileImage(multipartFile: MultipartFile?, userId: Long?): ProfileImageResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(USER_NOT_FOUND)

        user.fetchProfileImage().ifPresent { profileImage: ProfileImage ->
            val uploadedName = profileImage.uploadedName
            awss3Service.deleteOne(uploadedName)
        }
        val profileImage = awss3Service.uploadOne(multipartFile)
        profileImageRepository.save(profileImage)
        user.updateProfileImage(profileImage)
        val s3DownloadResult = awss3Service.downloadOne(profileImage)

        return ProfileImageResult.of(user, s3DownloadResult)
    }

    fun socialLoginUser(socialLoginRequest: SocialLoginRequest): UserTokenResult {
        val provider = inMemoryRepository.findByRegistrationId(socialLoginRequest.provider)
        val tokenResponse = getToken(socialLoginRequest.code, provider)
        val user = getUserProfile(socialLoginRequest.provider, tokenResponse.access_token, provider)
        val newToken = TokenProvider.createTokens(user.id, user.username, user.role)
        val refreshToken = newToken.refreshToken
        user.saveRefreshToken(refreshToken)
        userRepository.save(user)

        return UserTokenResult.of(user, newToken)
    }

    private fun getToken(code: String, provider: ClientRegistration): OAuthToken {
        return WebClient.create()
            .post()
            .uri(provider.providerDetails.tokenUri)
            .body(
                BodyInserters
                    .fromFormData("grant_type", "authorization_code")
                    .with("client_id", provider.clientId)
                    .with("redirect_uri", provider.redirectUri)
                    .with("code", code)
                    .with("client_secret", provider.clientSecret)
            )
            .retrieve()
            .bodyToMono(OAuthToken::class.java)
            .block()
    }

    private fun getUserProfile(providerName: String, code: String, provider: ClientRegistration): User {
        val userAttributes = getUserAttributes(provider, code)
        val oAuth2UserInfo = getOAuthUserInfo(providerName, userAttributes)
        val socialType = getSocialType(providerName)
        val providerId = oAuth2UserInfo.providerId
        val nickname = oAuth2UserInfo.nickname

        return userRepository.findBySocialId(providerId)
            ?.let { it }
            ?: saveUser(socialType, providerId, nickname)
    }

    private fun getUserAttributes(provider: ClientRegistration, code: String): Map<String, Any> {
        return WebClient.create()
            .get()
            .uri(provider.providerDetails.userInfoEndpoint.uri)
            .headers { header: HttpHeaders -> header.setBearerAuth(code) }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .block()
    }

    private fun getOAuthUserInfo(providerName: String, userAttributes: Map<String, Any>): OAuth2UserInfo {
        if (providerName == "kakao") {
            return KakaoUserProfile(userAttributes)
        }
        throw UserException(SOCIAL_PLATFORM_INVALID)
    }

    private fun getSocialType(providerName: String): SocialType {
        if (providerName == "kakao") {
            return SocialType.KAKAO
        }
        throw UserException(SOCIAL_PLATFORM_INVALID)
    }

    private fun saveUser(socialType: SocialType, providerId: String, nickname: String): User {
        val socialUser = User(
            UUID.randomUUID().toString(),
            Role.ROLE_USER,
            socialType,
            providerId,
            nickname
        )
        userRepository.save(socialUser)
        return socialUser
    }

    fun updateUserInfo(updateUserRequest: UpdateUserRequest, userId: Long?): UpdateUserResult {
        val user: User = userRepository.findById(userId!!)
            ?: throw UserException(USER_NOT_FOUND)
        user.updateUserInfo(updateUserRequest.nickname, updateUserRequest.contact)
        userRepository.save(user)

        return UpdateUserResult.of(user)
    }

    companion object {
        private fun getRequestRefreshToken(refreshRequest: RefreshRequest): String {
            val requestRefreshToken = TokenProvider.resolveToken(refreshRequest.refreshToken)
            validateToken(requestRefreshToken)
            return requestRefreshToken
        }

        private fun getDbRefreshToken(user: User): String {
            return user.refreshToken
                ?: throw UserException(TOKEN_NOT_FOUND)
        }
    }
}
