package com.googongill.aditory.controller.dto.user;

import com.googongill.aditory.domain.User;
import com.googongill.aditory.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Builder
public class SignupRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    private String contact;

    public User toEntity() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return new User(username, encodedPassword, Role.ROLE_USER, nickname, contact);
    }
}