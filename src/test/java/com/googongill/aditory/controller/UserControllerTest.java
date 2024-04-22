package com.googongill.aditory.controller;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.BusinessException;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.auth.PrincipalDetails;
import com.googongill.aditory.security.jwt.auth.PrincipalDetailsService;
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

import static com.googongill.aditory.common.code.UserErrorCode.ALREADY_EXISTING_USERNAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    public void setUp(@Value("${jwt.secret}") String TEST_SECRET) {
        tokenProvider = new TokenProvider(TEST_SECRET);
    }

    @Test
    public void signup_Success() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        SignResult signResult = createSignupResult();

        given(userService.createUser(
                signupRequest.getUsername(),
                signupRequest.getPassword(),
                Role.ROLE_USER,
                signupRequest.getNickname(),
                signupRequest.getContact()
                )
        ).willReturn(signResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .queryParam("username", signupRequest.getUsername())
                        .queryParam("password", signupRequest.getPassword())
                        .queryParam("nickname", signupRequest.getNickname())
                        .queryParam("contact", signupRequest.getContact())
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value("testNickname"));
    }

    @Test
    public void signup_Failed_Without_RequiredField() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        SignResult signResult = createSignupResult();

        given(userService.createUser(
                        signupRequest.getUsername(),
                        signupRequest.getPassword(),
                        Role.ROLE_USER,
                        signupRequest.getNickname(),
                        signupRequest.getContact()
                )
        ).willReturn(signResult);

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

//    Controller 가 아니라 Service 에서 검증해야 할듯?
//    @Test
    public void signup_Failed_With_ExistingUsername() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        SignResult signResult = createSignupResult();

        given(userService.createUser(
                signupRequest.getUsername(),
                signupRequest.getPassword(),
                Role.ROLE_USER,
                signupRequest.getNickname(),
                signupRequest.getContact()
                )
        ).willThrow(new BusinessException(ALREADY_EXISTING_USERNAME));

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/signup")
                        .with(csrf())
                        .queryParam("username", signupRequest.getUsername())
                        .queryParam("password", signupRequest.getPassword())
                        .queryParam("nickname", signupRequest.getNickname())
                        .queryParam("contact", signupRequest.getContact())
        );

        // then
        MvcResult existingUsernameResult = actions.andExpect(status().isConflict())
                .andReturn();
        Assertions.assertThat(existingUsernameResult.getResolvedException()).isExactlyInstanceOf(BusinessException.class); // UserErrorCode에 중복 회원 추가.
    }

    @Test
    public void login_Success() throws Exception {
        // given
        LoginRequest loginRequest = createLoginRequest();
        UserTokenResult userTokenResult = createUserTokenResult();

        given(userService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()
                )
        ).willReturn(userTokenResult);

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

        given(userService.login(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        ).willReturn(userTokenResult);

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
        willDoNothing().given(userService).logout(user.getUsername(), accessToken);

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

//    Token 에 대한 검사는 JwtFilter 해서 해줌.
//    @Test
    public void logout_Failed_Without_Token() throws Exception {
        // given
        User user = createUser();
        String accessToken = tokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole()).getAccessToken();

        given(principalDetailsService.loadUserByUsername(user.getUsername())).willReturn(new PrincipalDetails(user));
        willDoNothing().given(userService).logout(user.getUsername(), accessToken);


        // when
        ResultActions actions = mockMvc.perform(
                post("/users/logout")
        );

        // then
        MvcResult emptyTokenResult = actions.andExpect(status().isUnauthorized())
                .andReturn();
        Assertions.assertThat(emptyTokenResult.getResolvedException()).isExactlyInstanceOf(UserException.class);
    }

    @Test
    public void refresh_Success() throws Exception {
        // given
        String refreshToken = "refreshToken";
        UserTokenResult userTokenResult = createUserTokenResult();

        given(userService.refresh(refreshToken)).willReturn(userTokenResult);

        // when
        ResultActions actions = mockMvc.perform(
                post("/users/refresh")
                        .with(csrf())
                        .queryParam("refreshToken", "refreshToken")
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(0L))
                .andExpect(jsonPath("$.data.nickname").value("testNickname"));
    }

    private static SignupRequest createSignupRequest() {
        return SignupRequest.builder()
                .username("testUser")
                .password("testPw")
                .nickname("testNickname")
                .contact("010-1234-5678")
                .build();
    }

    private static SignResult createSignupResult() {
        return SignResult.builder()
                .userId(0L)
                .nickname("testNickname")
                .build();
    }

    private LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .username("testUser")
                .password("testPw")
                .build();
    }

    private UserTokenResult createUserTokenResult() {
        return UserTokenResult.builder()
                .userId(0L)
                .nickname("testNickname")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    private User createUser() {
        User user = new User("testUser", "testPw", Role.ROLE_USER, "testNickname", "010-1234-5678");
        return user;
    }
}