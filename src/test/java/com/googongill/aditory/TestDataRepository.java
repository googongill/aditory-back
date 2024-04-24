package com.googongill.aditory;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;

@Getter
@TestComponent
public class TestDataRepository {

    @MockBean
    private TokenProvider tokenProvider;

    public static User createUser() {
        User user = new User("testUser", "testPw", Role.ROLE_USER, "testNickname", "010-1234-5678");
        return user;
    }

    public static SignupRequest createSignupRequest() {
        return SignupRequest.builder()
                .username("testUser")
                .password("testPw")
                .nickname("testNickname")
                .contact("010-1234-5678")
                .build();
    }

    public static LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .username("testUser")
                .password("testPw")
                .build();
    }

    public static UserTokenResult createUserTokenResult() {
        return UserTokenResult.builder()
                .userId(0L)
                .nickname("testNickname")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    public static UserTokenResult createUserTokenResult(String accessToken, String refreshToken) {
        return UserTokenResult.builder()
                .userId(0L)
                .nickname("testNickname")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static RefreshRequest createRefreshRequest() {
        return RefreshRequest.builder()
                .userId(0L)
                .refreshToken("refreshToken")
                .build();
    }

    public static RefreshRequest createRefreshRequest(String refreshToken) {
        return RefreshRequest.builder()
                .userId(0L)
                .refreshToken(refreshToken)
                .build();
    }
}
