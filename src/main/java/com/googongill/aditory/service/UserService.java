package com.googongill.aditory.service;

import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.service.dto.user.LoginResult;
import com.googongill.aditory.service.dto.user.SignupResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public SignupResult createUser(String username, String password, Role role, String nickname, String contact) {
        return null;
    }

    public LoginResult login(String username, String password) {
        return null;
    }

    public void logout(String username, String accessToken) {
    }
}
