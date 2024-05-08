package com.googongill.aditory;

import com.googongill.aditory.controller.dto.link.CreateLinkRequest;
import com.googongill.aditory.controller.dto.link.UpdateLinkRequest;
import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.controller.dto.user.UpdateUserRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.Link;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.domain.enums.SocialType;
import com.googongill.aditory.external.chatgpt.dto.AutoCategorizeResult;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import com.googongill.aditory.service.dto.link.LinkInfo;
import com.googongill.aditory.service.dto.link.LinkResult;
import com.googongill.aditory.service.dto.link.ReminderResult;
import com.googongill.aditory.service.dto.user.ProfileImageResult;
import com.googongill.aditory.service.dto.user.UpdateUserResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;

@Getter
@TestComponent
public class TestDataRepository {

    public static User createUser() {
        return new User("testuser",
                "testuserpw",
                Role.ROLE_USER,
                SocialType.LOCAL,
                "tester nickname",
                "010-1234-5678");
    }

    public static Category createCategory() {
        return new Category(
                "development",
                createUser());
    }

    public static Link createLink(Category category, User user) {
        return new Link(
                "C++ library",
                "How to use C++ library's function",
                "https://www.c++library.com",
                category,
                user);
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

    public static CreateLinkRequest createCreateLinkRequest() {
        return CreateLinkRequest.builder()
                .autoComplete(false)
                .title("C++ library")
                .summary("How to use C++ library's function")
                .url("https://www.c++library.com")
                .categoryId(0L)
                .build();
    }

    public static CreateLinkRequest createAutoCreateLinkRequest() {
        return CreateLinkRequest.builder()
                .autoComplete(true)
                .url("https://www.c++library.com")
                .build();
    }

    public static LinkResult createLinkResult() {
        return LinkResult.builder()
                .linkId(0L)
                .categoryId(0L)
                .createdAt(now())
                .build();
    }

    public AutoCategorizeResult createAuthCategorizeResult() {
        return AutoCategorizeResult.builder()
                .title("auto title")
                .summary("auto summary")
                .categoryName("development")
                .build();
    }

    public static ReminderResult createReminderResult() {
        return ReminderResult.builder()
                .linkList(Arrays.asList(
                        LinkInfo.builder()
                                .linkId(0L)
                                .title("C++ library")
                                .summary("How to use C++ library's function")
                                .linkState(false)
                                .createdAt(now())
                                .lastModifiedAt(now())
                                .build()
                ))
                .build();
    }

    public static UpdateLinkRequest createUpdateLinkRequest() {
        return UpdateLinkRequest.builder()
                .title("updated Link title")
                .summary("updated Link summary")
                .url("https://www.updatedLink.com")
                .categoryId(0L)
                .build();
    }
}
