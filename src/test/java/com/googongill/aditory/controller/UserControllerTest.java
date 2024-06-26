package com.googongill.aditory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googongill.aditory.TestDataRepository;
import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.user.request.LoginRequest;
import com.googongill.aditory.controller.dto.user.request.RefreshRequest;
import com.googongill.aditory.controller.dto.user.request.SignupRequest;
import com.googongill.aditory.controller.dto.user.request.UpdateUserRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.ProfileImage;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.external.s3.AWSS3Service;
import com.googongill.aditory.external.s3.dto.S3DownloadResult;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.security.jwt.user.PrincipalDetailsService;
import com.googongill.aditory.service.UserService;
import com.googongill.aditory.service.dto.user.ProfileImageResult;
import com.googongill.aditory.service.dto.user.UpdateUserResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignupResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.Optional;

import static com.googongill.aditory.common.code.SuccessCode.LOGOUT_SUCCESS;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
@ComponentScan(basePackages = "com.googongill.aditory.security")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private AWSS3Service awss3Service;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private PrincipalDetails principalDetails;
    @MockBean
    private PrincipalDetailsService principalDetailsService;
    @InjectMocks
    private TestDataRepository testDataRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private User user;
    private String accessToken = "";

    @BeforeEach
    public void init(@Value("${jwt.test-secret}") String TEST_SECRET) throws Exception {
        tokenProvider = new TokenProvider(TEST_SECRET);

        user = testDataRepository.createUser();
        TestUtils.setEntityId(0L, user);
        accessToken = tokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole()).getAccessToken();
        given(principalDetailsService.loadUserByUsername(user.getUsername())).willReturn(new PrincipalDetails(user));
        given(principalDetails.getUserId()).willReturn(user.getId());
    }

    @Test
    public void signup_Success() throws Exception {
        // given
        SignupRequest signupRequest = testDataRepository.createSignupRequest();
        List<Category> createdCategories = testDataRepository.createCategories();
        SignupResult signupResult = SignupResult.of(signupRequest.toEntity(), createdCategories);

        String successSignupRequestJson = objectMapper.writeValueAsString(signupRequest);

        given(userService.createUser(signupRequest)).willReturn(signupResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successSignupRequestJson)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nickname").value(signupRequest.getNickname()))
                .andExpect(jsonPath("$.data.userCategories[0].categoryName").value(createdCategories.get(0).getCategoryName()));
    }

    @Test
    public void signup_Failed_Without_RequiredField() throws Exception {
        // given
        SignupRequest signupRequest = testDataRepository.createSignupRequest();
        List<Category> createdCategories = testDataRepository.createCategories();
        SignupResult signupResult = SignupResult.of(signupRequest.toEntity(), createdCategories);

        signupRequest.setPassword("");
        String failSignupRequestJson = objectMapper.writeValueAsString(signupRequest);

        given(userService.createUser(signupRequest)).willReturn(signupResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failSignupRequestJson)
        );

        // then
        MvcResult emptyPasswordResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyPasswordResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void login_Success() throws Exception {
        // given
        LoginRequest loginRequest = testDataRepository.createLoginRequest();
        UserTokenResult userTokenResult = testDataRepository.createUserTokenResult();

        String successLoginRequestJson = objectMapper.writeValueAsString(loginRequest);

        given(userService.loginUser(loginRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successLoginRequestJson)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value(userTokenResult.getNickname()));
    }

    @Test
    public void login_Failed_Without_RequiredField() throws Exception {
        // given
        LoginRequest loginRequest = testDataRepository.createLoginRequest();
        UserTokenResult userTokenResult = testDataRepository.createUserTokenResult();

        loginRequest.setPassword("");
        String failLoginRequestJson = objectMapper.writeValueAsString(loginRequest);

        given(userService.loginUser(loginRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failLoginRequestJson)
        );

        // then
        MvcResult emptyPasswordResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyPasswordResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void logout_Success() throws Exception {
        // given
        willDoNothing().given(userService).logoutUser(user.getUsername(), accessToken);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LOGOUT_SUCCESS.getMessage()));
    }

    @Test
    public void refresh_Success() throws Exception {
        // given
        RefreshRequest refreshRequest = testDataRepository.createRefreshRequest("refreshToken");
        UserTokenResult userTokenResult = testDataRepository.createUserTokenResult();

        String successSignupRequestJson = objectMapper.writeValueAsString(refreshRequest);

        given(userService.refreshUser(refreshRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successSignupRequestJson)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value(userTokenResult.getNickname()));
    }

    @Test
    public void updateProfileImage_Success() throws Exception {
        // given
        MockMultipartFile mockMultipartFile = testDataRepository.createMockMultipartFile();
        ProfileImageResult profileImageResult = testDataRepository.createProfileImageResult();

        given(userService.updateProfileImage(mockMultipartFile, principalDetails.getUserId())).willReturn(profileImageResult);

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/users/profile-image")
                        .file(mockMultipartFile)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.data.s3DownloadResult.url").value(profileImageResult.getS3DownloadResult().getUrl()));
    }

    @Test
    public void updateProfileImage_Failed_Without_File() throws Exception {
        // given
        MockMultipartFile mockMultipartFile = testDataRepository.createMockMultipartFile();
        ProfileImageResult profileImageResult = testDataRepository.createProfileImageResult();

        given(userService.updateProfileImage(mockMultipartFile, principalDetails.getUserId())).willReturn(profileImageResult);

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/users/profile-image")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        MvcResult emptyFileResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyFileResult.getResolvedException()).isExactlyInstanceOf(MissingServletRequestPartException.class);
    }

    @Test
    public void getUserInfo_Success() throws Exception {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        ResultActions actions = mockMvc.perform(
                get("/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.nickname").value(user.getNickname()));
    }

    @Test
    public void getProfileImage_Success() throws Exception {
        // given
        ProfileImage profileImage = testDataRepository.createProfileImage();
        user.updateProfileImage(profileImage);
        userRepository.save(user);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        S3DownloadResult s3DownloadResult = testDataRepository.createS3DownloadResult();
        given(awss3Service.downloadOne(profileImage)).willReturn(s3DownloadResult);

        // when
        ResultActions actions = mockMvc.perform(
                get("/users/profile-image")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.s3DownloadResult.originalName").value(profileImage.getOriginalName()));
    }

    @Test
    public void getProfileImage_Failed_NotExistingProfileImage() throws Exception {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        ResultActions actions = mockMvc.perform(
                get("/users/profile-image")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        MvcResult notExistingProfileImageResult = actions.andExpect(status().isNotFound()).andReturn();
        Assertions.assertThat(notExistingProfileImageResult.getResolvedException()).isExactlyInstanceOf(UserException.class);
    }

    @Test
    public void updateUserInfo_Success() throws Exception {
        // given
        UpdateUserRequest updateUserRequest = testDataRepository.createUpdateUserRequest();
        UpdateUserResult updateUserResult = testDataRepository.createUpdateUserResult();
        String successUpdateUserRequestJson = objectMapper.writeValueAsString(updateUserRequest);

        given(userService.updateUserInfo(updateUserRequest, principalDetails.getUserId())).willReturn(updateUserResult);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(successUpdateUserRequestJson)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(updateUserRequest.getNickname()))
                .andExpect(jsonPath("$.data.contact").value(updateUserRequest.getContact()));
    }

    @Test
    public void updateUserInfo_Failed_Without_RequiredField() throws Exception {
        // given
        UpdateUserRequest updateUserRequest = testDataRepository.createUpdateUserRequest();
        UpdateUserResult updateUserResult = testDataRepository.createUpdateUserResult();

        updateUserRequest.setNickname("");
        String failUpdateUserRequestJson = objectMapper.writeValueAsString(updateUserRequest);

        given(userService.updateUserInfo(updateUserRequest, principalDetails.getUserId())).willReturn(updateUserResult);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failUpdateUserRequestJson)
        );

        // then
        MvcResult emptyNicknameResult = actions.andExpect(status().isBadRequest()).andReturn();
        Assertions.assertThat(emptyNicknameResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void signout_Success() throws Exception {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        willDoNothing().given(userRepository).delete(user);

        // when
        ResultActions actions = mockMvc.perform(
                delete("/users/signout")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }
}