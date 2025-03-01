package com.googongill.aditory.security.jwt.user

import com.googongill.aditory.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class PrincipalDetails(val user: User) : UserDetails {
    // 해당 유저 권한 반환
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collect = ArrayList<GrantedAuthority>()
        collect.add(GrantedAuthority { user.role.toString() })
        return collect
    }

    val userId: Long?
        get() = user.id
    val nickname: String?
        get() = user.nickname

    override fun getUsername(): String {
        return user.username
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
