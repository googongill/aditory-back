package com.googongill.aditory.security.jwt.user

import com.googongill.aditory.domain.User
import com.googongill.aditory.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class PrincipalDetailsService(val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return Optional.ofNullable(userRepository.findByUsername(username))
            .map { user : User -> PrincipalDetails(user) }
            .orElseThrow { UsernameNotFoundException("사용자 $username 를 찾을 수 없습니다.") }
    }

}
