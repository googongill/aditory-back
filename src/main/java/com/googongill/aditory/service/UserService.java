package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.user.request.*;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.external.s3.AWSS3Service;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.ProfileImageRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.dto.JwtResult;
import com.googongill.aditory.security.oauth.KakaoUserProfile;
import com.googongill.aditory.security.oauth.OAuth2UserInfo;
import com.googongill.aditory.service.dto.user.OAuthToken;
import com.googongill.aditory.service.dto.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.UserErrorCode.*;
import static com.googongill.aditory.security.jwt.TokenProvider.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final AWSS3Service awss3Service;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ProfileImageRepository profileImageRepository;
    private final InMemoryClientRegistrationRepository inMemoryRepository;

    public SignupResult createUser(SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent())
            throw new UserException(ALREADY_EXISTING_USERNAME);
        User createduser = signupRequest.toEntity();
        userRepository.save(createduser);
        List<Category> createdCategories = signupRequest.getUserCategories().stream()
                .map(categoryName -> {
                    Category category = new Category(categoryName, categoryName, createduser);
                    return categoryRepository.save(category);
                })
                .collect(Collectors.toList());

        createduser.addCategories(createdCategories);
        return SignupResult.of(createduser, createdCategories);
    }

    public UserTokenResult loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_INVALID);
        }
        JwtResult jwtResult = TokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtResult.getRefreshToken();
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);
        return UserTokenResult.of(user, jwtResult);
    }

    public void logoutUser(String accessToken, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        user.deleteRefreshToken();
        userRepository.save(user);
    }

    public UserTokenResult refreshUser(RefreshRequest refreshRequest) {
        String requestRefreshToken = getRequestRefreshToken(refreshRequest);
        User user = userRepository.findById(refreshRequest.getUserId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        String dbRefreshToken = getDbRefreshToken(user);
        if (!requestRefreshToken.equals(dbRefreshToken)) {
            throw new UserException(TOKEN_INVALID);
        }
        JwtResult newToken = createTokens(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = newToken.getRefreshToken();
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);
        return UserTokenResult.of(user, newToken);
    }

    private static String getRequestRefreshToken(RefreshRequest refreshRequest) {
        String requestRefreshToken = resolveToken(refreshRequest.getRefreshToken());
        validateToken(requestRefreshToken);
        return requestRefreshToken;
    }

    private static String getDbRefreshToken(User user) {
        String dbRefreshToken = user.getRefreshToken();
        if (dbRefreshToken == null)  {
            throw new UserException(TOKEN_NOT_FOUND);
        }
        return dbRefreshToken;
    }

    public ProfileImageResult updateProfileImage(MultipartFile multipartFile, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        user.getProfileImage().ifPresent(profileImage -> {
            String uploadedName = profileImage.getUploadedName();
            awss3Service.deleteOne(uploadedName);
        });

        ProfileImage profileImage = awss3Service.uploadOne(multipartFile);
        profileImageRepository.save(profileImage);
        user.updateProfileImage(profileImage);

        S3DownloadResult s3DownloadResult = awss3Service.downloadOne(profileImage);

        return ProfileImageResult.of(user, s3DownloadResult);
    }

    public UserTokenResult socialLoginUser(SocialLoginRequest socialLoginRequest) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(socialLoginRequest.getProvider());
        OAuthToken tokenResponse = getToken(socialLoginRequest.getCode(), provider);
        User user = getUserProfile(socialLoginRequest.getProvider(), tokenResponse.getAccess_token(), provider);

        JwtResult newToken = createTokens(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = newToken.getRefreshToken();
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);

        return UserTokenResult.of(user, newToken);
    }

    private OAuthToken getToken(String code, ClientRegistration provider) {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", provider.getClientId())
                        .with("redirect_uri", provider.getRedirectUri())
                        .with("code", code)
                        .with("client_secret", provider.getClientSecret()))
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .block();
    }

    public User getUserProfile(String providerName, String code, ClientRegistration provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, code);
        OAuth2UserInfo oAuth2UserInfo = getOAuthUserInfo(providerName, userAttributes);
        SocialType socialType = getSocialType(providerName);

        String providerId = oAuth2UserInfo.getProviderId();
        String nickname = oAuth2UserInfo.getNickname();

        Optional<User> userEntity = userRepository.findBySocialId(providerId);

        return userEntity
                .orElseGet(() -> saveUser(socialType, providerId, nickname));
    }

    private Map<String, Object> getUserAttributes(ClientRegistration provider, String code) {
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(code))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    private OAuth2UserInfo getOAuthUserInfo(String providerName, Map<String, Object> userAttributes) {
        if (providerName.equals("kakao")) {
            return new KakaoUserProfile(userAttributes);
        }
        throw new UserException(SOCIAL_PLATFORM_INVALID);
    }

    private SocialType getSocialType(String providerName) {
        if (providerName.equals("kakao")) {
            return SocialType.KAKAO;
        }
        throw new UserException(SOCIAL_PLATFORM_INVALID);
    }

    private User saveUser(SocialType socialType, String providerId, String nickname) {
        User socialUser = new User(
                String.valueOf(UUID.randomUUID()),
                Role.ROLE_USER,
                socialType,
                providerId,
                nickname);
        userRepository.save(socialUser);
        return socialUser;
    }

    public UpdateUserResult updateUserInfo(UpdateUserRequest updateUserRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        user.updateUserInfo(updateUserRequest.getNickname(), updateUserRequest.getContact());
        userRepository.save(user);
        return UpdateUserResult.of(user);
    }
}
