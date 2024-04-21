package com.googongill.aditory.controller;

import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.BusinessException;
import com.googongill.aditory.security.jwt.auth.PrincipalDetailsService;
import com.googongill.aditory.service.UserService;
import com.googongill.aditory.service.dto.SignupResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private PrincipalDetailsService principalDetailsService;

    @Test
    public void signup_Success() throws Exception {
        // given
        SignupRequest signupRequest = createSignupRequest();
        SignupResult signupResult = createSignupResult();

        given(userService.createUser(
                        signupRequest.getUsername(),
                        signupRequest.getPassword(),
                        Role.ROLE_USER,
                        signupRequest.getNickname(),
                        signupRequest.getContact()
                )
        ).willReturn(signupResult);

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
        SignupResult signupResult = createSignupResult();

        given(userService.createUser(
                        signupRequest.getUsername(),
                        signupRequest.getPassword(),
                        Role.ROLE_USER,
                        signupRequest.getNickname(),
                        signupRequest.getContact()
                )
        ).willReturn(signupResult);

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
        SignupResult signupResult = createSignupResult();

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

    private static SignupRequest createSignupRequest() {
        return SignupRequest.builder()
                .username("testUser")
                .password("testPw")
                .nickname("testNickname")
                .contact("010-1234-5678")
                .build();
    }

    private static SignupResult createSignupResult() {
        return SignupResult.builder()
                .userId(0L)
                .nickname("testNickname")
                .build();
    }
}