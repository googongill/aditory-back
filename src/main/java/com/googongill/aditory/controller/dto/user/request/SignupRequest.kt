package com.googongill.aditory.controller.dto.user.request

import com.googongill.aditory.domain.User
import com.googongill.aditory.domain.enums.Role
import com.googongill.aditory.domain.enums.SocialType
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class SignupRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
    @NotBlank
    val nickname: String,
    val contact: String? = null,
    val userCategories: List<String>
) {

    fun toEntity(): User {
        val bCryptPasswordEncoder = BCryptPasswordEncoder()
        val encodedPassword = bCryptPasswordEncoder.encode(password)
        return User(username, encodedPassword, Role.ROLE_USER, SocialType.LOCAL, nickname, contact)
    }

}
