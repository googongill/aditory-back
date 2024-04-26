package com.googongill.aditory.controller;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import com.googongill.aditory.security.jwt.user.PrincipalDetailsService;
import com.googongill.aditory.service.UserService;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static com.googongill.aditory.TestDataRepository.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WithMockUser
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
@ComponentScan(basePackages = "com.googongill.aditory.security")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PrincipalDetails principalDetails;
    @MockBean
    private PrincipalDetailsService principalDetailsService;
    @MockBean
    private TokenProvider tokenProvider;

    @BeforeEach
    public void init(@Value("${jwt.test-secret}") String TEST_SECRET) {
        tokenProvider = new TokenProvider(TEST_SECRET);
    }

    @Test
    public void signup_Success() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        List<Category> createdCategories = createCategories();
        SignResult signResult = SignResult.of(signupRequest.toEntity(), createdCategories);

        given(userService.createUser(signupRequest)).willReturn(signResult);

        // when
        String userCategoriesParam = String.join(",", signupRequest.getUserCategories());
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .queryParam("username", signupRequest.getUsername())
                        .queryParam("password", signupRequest.getPassword())
                        .queryParam("nickname", signupRequest.getNickname())
                        .queryParam("contact", signupRequest.getContact())
                        .queryParam("userCategories", userCategoriesParam)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nickname").value(signupRequest.getNickname()));
    }

    @Test
    public void signup_Failed_Without_RequiredField() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        List<Category> categories = createCategories();
        SignResult signResult = SignResult.of(signupRequest.toEntity(), categories);

        given(userService.createUser(signupRequest)).willReturn(signResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .queryParam("username", signupRequest.getUsername())
                        .queryParam("password", "")
                        .queryParam("nickname", signupRequest.getNickname())
                        .queryParam("contact", signupRequest.getContact())
        );

        // then
        MvcResult emptyPasswordResult = actions.andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertThat(emptyPasswordResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void login_Success() throws Exception {
        // given
        LoginRequest loginRequest = createLoginRequest();
        UserTokenResult userTokenResult = createUserTokenResult();

        given(userService.loginUser(loginRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/login")
                        .with(csrf())
                        .queryParam("username", "testUser")
                        .queryParam("password", "testPw")
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value("testNickname"));
    }

    @Test
    public void login_Failed_Without_RequireField() throws Exception {
        // given
        LoginRequest loginRequest = createLoginRequest();
        UserTokenResult userTokenResult = createUserTokenResult();

        given(userService.loginUser(loginRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/login")
                        .with(csrf())
                        .queryParam("username", "testUser")
                        .queryParam("password", "")
        );

        // then
        MvcResult emptyPasswordResult = actions.andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertThat(emptyPasswordResult.getResolvedException()).isExactlyInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void logout_Success() throws Exception {
        // given
        User user = createUser();
        String accessToken = tokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole()).getAccessToken();

        given(principalDetailsService.loadUserByUsername(user.getUsername())).willReturn(new PrincipalDetails(user));
        willDoNothing().given(userService).logoutUser(user.getUsername(), accessToken);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(content().json("{message: '로그아웃에 성공했습니다.'}"));
    }

    @Test
    public void refresh_Success() throws Exception {
        // given
        RefreshRequest refreshRequest = createRefreshRequest();
        UserTokenResult userTokenResult = createUserTokenResult();

        given(userService.refreshUser(refreshRequest)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/refresh")
                        .with(csrf())
                        .queryParam("userId", refreshRequest.getUserId().toString())
                        .queryParam("refreshToken", refreshRequest.getRefreshToken())
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value("testNickname"));
    }
}