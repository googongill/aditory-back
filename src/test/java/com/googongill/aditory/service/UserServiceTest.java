package com.googongill.aditory.service;

import com.googongill.aditory.TestUtils;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.user.SignResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.googongill.aditory.common.code.UserErrorCode.ALREADY_EXISTING_USERNAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
        org.junit.jupiter.api.Assertions.assertEquals(ALREADY_EXISTING_USERNAME, exception.getErrorCode());
        // then
    }

    private SignupRequest createSignupRequest() {
        return SignupRequest.builder()
                .username("testUser")
                .password("testPw")
                .nickname("testNickname")
                .contact("010-1234-5678")
                .build();
    }
}