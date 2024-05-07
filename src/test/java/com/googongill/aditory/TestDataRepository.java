package com.googongill.aditory;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.controller.dto.user.UpdateUserRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import com.googongill.aditory.service.dto.user.ProfileImageResult;
import com.googongill.aditory.service.dto.user.UpdateUserResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.mock.web.MockMultipartFile;

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

    public static MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "multipartFile",
                "profile_image.jpg",
                "image/jpeg",
                "content".getBytes()
        );
    }

    public static ProfileImageResult createProfileImageResult() {
        return ProfileImageResult.builder()
                .userId(0L)
                .username("testuser")
                .nickname("tester nickname")
                .s3DownloadResult(createS3DownloadResult())
                .build();
    }

    public static ProfileImage createProfileImage() {
        return new ProfileImage("profile_image.jpg", "12345678.jpg");
    }

    public static S3DownloadResult createS3DownloadResult() {
        return S3DownloadResult.builder()
                .profileImageId(0L)
                .originalName("profile_image.jpg")
                .url("s3.amazonaws.com")
                .build();
    }

    public static UpdateUserRequest createUpdateUserRequest() {
        return UpdateUserRequest.builder()
                .nickname("updated test nickname")
                .contact("010-5678-1234")
                .build();
    }

    public static UpdateUserResult createUpdateUserResult() {
        return UpdateUserResult.builder()
                .userId(0L)
                .nickname("updated test nickname")
                .contact("010-5678-1234")
                .build();
    }
}
