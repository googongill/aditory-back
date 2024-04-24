package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.dto.JwtDto;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.googongill.aditory.common.code.UserErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignResult createUser(SignupRequest signupRequest) {
        // 이미 존재하는 username 존재하는지 확인
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent())
            throw new UserException(ALREADY_EXISTING_USERNAME);
        return SignResult.of(userRepository.save(signupRequest.toEntity()));
    }

    public UserTokenResult loginUser(LoginRequest loginRequest) {
        // username 확인
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // 비밀번호 일치 확인
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException(INVALID_PASSWORD);
        }
        // 토큰 발급
        JwtDto jwtDto = TokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole());
        // User 에 refresh Token 저장
        String refreshToken = jwtDto.getRefreshToken();
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);
        // 로그인 완료
        return UserTokenResult.of(user, jwtDto);
    }

    public void logoutUser(String username, String accessToken) {
        // username 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // refreshToken 삭제
        user.deleteRefreshToken();
        userRepository.save(user);
    }

    public UserTokenResult refreshUser(RefreshRequest refreshRequest) {
        return null;
    }
}
