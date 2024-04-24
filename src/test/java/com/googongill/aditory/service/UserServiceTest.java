package com.googongill.aditory.service;

import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.dto.JwtDto;
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

import java.util.Optional;

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private TokenProvider tokenProvider;

    @BeforeEach
    public void init() {
        // secret 을 불러오지 못해서 임의로 secret 넣음 -> 추후 수정
        tokenProvider = new TokenProvider("7Jyg7KCA7ISc67mE7Iqk7YWM7Iqk7Yq47Jqp7Iuc7YGs66a/7YKk6rCA7ZWE7JqU7ZW07ISc7JWE66y06rCS7J2064KY64Sj7Ja07ISc7JWU7Zi47ZmU7ZaI7J2M");
    }

    @Test
    public void createUser_Success() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = signupRequest.toEntity();
        Long expectedUserId = 123L;
        TestUtils.setEntityId(expectedUserId, user);

        doAnswer(invocation -> {
            TestUtils.setEntityId(expectedUserId, user);
            return user;
        }).when(userRepository).save(any(User.class));

        // when
        SignResult savedUser = userService.createUser(signupRequest);

        // then
        Assertions.assertThat(savedUser.getUserId()).isEqualTo(expectedUserId);
    }

    @Test
    public void createUser_Failed_ExistingUsername() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User existingUser = new User(signupRequest.getUsername(), "existingPw", Role.ROLE_USER, "existingNickname", "010-1234-5678");

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
        JwtDto jwtDto = new JwtDto("accessToken", "refreshToken");

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
}