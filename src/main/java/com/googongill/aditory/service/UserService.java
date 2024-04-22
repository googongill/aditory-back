package com.googongill.aditory.service;

import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.service.dto.user.UserTokenResult;
import com.googongill.aditory.service.dto.user.SignResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public SignResult createUser(String username, String password, Role role, String nickname, String contact) {
        return null;
    }

    public UserTokenResult login(String username, String password) {
        return null;
    }

    public void logout(String username, String accessToken) {
    }

    public UserTokenResult refresh(String refreshToken) {
        return null;
    }
}
