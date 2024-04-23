package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.googongill.aditory.common.code.UserErrorCode.ALREADY_EXISTING_USERNAME;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignResult createUser(SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent())
            throw new UserException(ALREADY_EXISTING_USERNAME);
        return SignResult.of(userRepository.save(signupRequest.toEntity()));
    }

    public UserTokenResult login(LoginRequest loginRequest) {
        return null;
    }

    public void logout(String username, String accessToken) {
    }

    public UserTokenResult refresh(RefreshRequest refreshRequest) {
        return null;
    }
}
