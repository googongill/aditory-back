package com.googongill.aditory.service;

import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.dto.JwtResult;
import com.googongill.aditory.service.dto.user.SignResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.googongill.aditory.TestDataRepository.*;
import static com.googongill.aditory.common.code.UserErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private TokenProvider tokenProvider;

    @BeforeEach
    public void init() {
        // test 를 위한 더미 secret 주입 -> 추후 수정
        tokenProvider = new TokenProvider("YWRpdG9yeS1iYWNrZW5kLXRlc3QtZ29vZ29uZ2lsbC1zZXVuZ2p1bmh3YW5nLWV1Z2VuZWxlZS1qaWV1bnBhcms=");
    }

    @Test
    public void createUser_Success() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = signupRequest.toEntity();
        Long expectedUserId = 123L;
        TestUtils.setEntityId(expectedUserId, user);

        given(userRepository.findByUsername(signupRequest.getUsername())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);

        List<String> categoryNames = signupRequest.getUserCategories();
        List<Category> createdCategories = categoryNames.stream()
                .map(categoryName -> {
                    Category category = new Category(categoryName, user);
                    return category;
                })
                .collect(Collectors.toList());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            return category;
        });

        // when
        SignResult savedUser = userService.createUser(signupRequest);

        // then
        Assertions.assertThat(savedUser.getNickname()).isEqualTo(signupRequest.getNickname());
    }
    @Test
    public void createUser_Failed_ExistingUsername() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User existingUser = new User(signupRequest.getUsername(),
                "existingPw",
                Role.ROLE_USER, SocialType.LOCAL,
                "존재하는 유저",
                "010-1234-5678");

        given(userRepository.findByUsername(signupRequest.getUsername())).willReturn(Optional.of(existingUser));

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> userService.createUser(signupRequest));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(ALREADY_EXISTING_USERNAME, exception.getErrorCode());
    }

    @Test
    public void loginUser_Success_ValidToken() throws Exception {
        // given
        User user = createUser();
        LoginRequest loginRequest = createLoginRequest();
        UserTokenResult targetResult = createUserTokenResult();

        given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())).willReturn(true);

        // when
        UserTokenResult actualResult = userService.loginUser(loginRequest);

        // then
        verify(userRepository, times(1)).save(user); // save refreshToken check
        Assertions.assertThat(actualResult).isNotNull();
        Assertions.assertThat(actualResult.getNickname()).isEqualTo(targetResult.getNickname());
    }

    @Test
    public void loginUser_Failed_NotExistingUser() throws Exception {
        // given
        LoginRequest loginRequest = createLoginRequest();

        given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.empty());

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> userService.loginUser(loginRequest));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void loginUser_Failed_IncorrectPassword() throws Exception {
        // given
        LoginRequest loginRequest = createLoginRequest();
        User user = createUser();

        given(userRepository.findByUsername(loginRequest.getUsername())).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())).willReturn(false);

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> userService.loginUser(loginRequest));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    public void logoutUser_Success_DeletingRefreshToken() throws Exception {
        // given
        User user = createUser();
        String accessToken = "accessToken";

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        userService.logoutUser(user.getUsername(), accessToken);

        // then
        verify(userRepository, times(1)).save(user);
        org.junit.jupiter.api.Assertions.assertNull(user.getRefreshToken());
    }

    @Test
    public void logoutUser_Failed_NotExistingUser() throws Exception {
        // given
        String username = "nonExistingUsername";
        String accessToken = "accessToken";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        UserException exception = org.junit.jupiter.api.Assertions.assertThrows(UserException.class, () -> userService.logoutUser(username, accessToken));

        // then
        org.junit.jupiter.api.Assertions.assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void refreshUser_Success() throws Exception {
        // given
        User user = createUser();
        TestUtils.setEntityId(0L, user);
        JwtResult jwtResult = tokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole());
        user.saveRefreshToken(jwtResult.getRefreshToken());

        RefreshRequest refreshRequest = createRefreshRequest("Bearer " + jwtResult.getRefreshToken());
        UserTokenResult targetResult = createUserTokenResult(jwtResult.getAccessToken(), jwtResult.getRefreshToken());

        given(userRepository.findById(refreshRequest.getUserId())).willReturn(Optional.of(user));

        // when
        UserTokenResult actualResult = userService.refreshUser(refreshRequest);

        // then
        Assertions.assertThat(actualResult).isNotNull();
        Assertions.assertThat(actualResult.getUserId()).isEqualTo(targetResult.getUserId());
        Assertions.assertThat(actualResult.getNickname()).isEqualTo(targetResult.getNickname());
    }
}