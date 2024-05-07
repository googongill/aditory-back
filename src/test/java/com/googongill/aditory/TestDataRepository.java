package com.googongill.aditory;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;

import java.util.Arrays;
import java.util.List;

@Getter
@TestComponent
public class TestDataRepository {

    public static User createUser() {
        User user = new User("testuser",
                "testuserpw",
                Role.ROLE_USER, SocialType.LOCAL,
                "tester nickname",
                "010-1234-5678");
        return user;
    }

    public static List<Category> createCategories() {
        Category category1 = new Category("development");
        Category category2 = new Category("information");
        List<Category> createdCategories = Arrays.asList(category1, category2);
        return createdCategories;
    }

    public static SignupRequest createSignupRequest() {
        return SignupRequest.builder()
                .username("testuser")
                .password("testuserpw")
                .nickname("tester nickname")
                .contact("010-1234-5678")
                .userCategories(Arrays.asList("development", "information"))
                .build();
    }

    public static LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .username("testuser")
                .password("testuserpw")
                .build();
    }

    public static UserTokenResult createUserTokenResult() {
        return UserTokenResult.builder()
                .userId(0L)
                .nickname("tester nickname")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    public static UserTokenResult createUserTokenResult(String accessToken, String refreshToken) {
        return UserTokenResult.builder()
                .userId(0L)
                .nickname("tester nickname")
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
