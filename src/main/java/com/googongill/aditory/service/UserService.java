package com.googongill.aditory.service;

import com.googongill.aditory.controller.dto.user.LoginRequest;
import com.googongill.aditory.controller.dto.user.RefreshRequest;
import com.googongill.aditory.controller.dto.user.SignupRequest;
import com.googongill.aditory.domain.Category;
import com.googongill.aditory.domain.User;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.repository.CategoryRepository;
import com.googongill.aditory.repository.UserRepository;
import com.googongill.aditory.security.jwt.TokenProvider;
import com.googongill.aditory.security.jwt.dto.JwtResult;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.googongill.aditory.common.code.UserErrorCode.*;
import static com.googongill.aditory.security.jwt.TokenProvider.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignResult createUser(SignupRequest signupRequest) {
        // 이미 존재하는 username 존재하는지 확인
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent())
            throw new UserException(ALREADY_EXISTING_USERNAME);
        // 사용자 생성
        User createduser = signupRequest.toEntity();
        userRepository.save(createduser);
        // 카테고리 생성
        List<Category> createdCategories = signupRequest.getUserCategories().stream()
                .map(categoryName -> {
                    Category category = new Category(categoryName, createduser);
                    return categoryRepository.save(category);
                })
                .collect(Collectors.toList());

        // 카테고리 추가 (연관관계 메서드)
        createduser.addCategories(createdCategories);
        return SignResult.of(createduser, createdCategories);
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
        JwtResult jwtResult = TokenProvider.createTokens(user.getId(), user.getUsername(), user.getRole());
        // User 에 refresh Token 저장
        String refreshToken = jwtResult.getRefreshToken();
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);
        // 로그인 완료
        return UserTokenResult.of(user, jwtResult);
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
        // request refreshToken 검증
        String requestRefreshToken = getRequestRefreshToken(refreshRequest);
        // userId 확인
        User user = userRepository.findById(refreshRequest.getUserId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        // db 의 refreshToken
        String dbRefreshToken = getDbRefreshToken(user);
        // db 와 request 토큰 일치 확인
        if (!requestRefreshToken.equals(dbRefreshToken)) {
            throw new UserException(TOKEN_INVALID);
        }
        JwtResult newToken = createTokens(user.getId(), user.getUsername(), user.getRole());
        return UserTokenResult.of(user, newToken);
    }

    private static String getRequestRefreshToken(RefreshRequest refreshRequest) {
        String requestRefreshToken = resolveToken(refreshRequest.getRefreshToken());
        validateToken(requestRefreshToken);
        return requestRefreshToken;
    }

    private static String getDbRefreshToken(User user) {
        String dbRefreshToken = user.getRefreshToken();
        if (dbRefreshToken == null)  {
            throw new UserException(TOKEN_NOT_FOUND);
        }
        return dbRefreshToken;
    }
}
