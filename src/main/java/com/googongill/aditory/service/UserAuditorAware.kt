package com.googongill.aditory.service

import com.googongill.aditory.security.jwt.user.PrincipalDetails
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication

        return if (authentication == null || !authentication.isAuthenticated) {
            Optional.empty()
        } else try {
            val principalDetails = authentication.principal as PrincipalDetails
            Optional.of(principalDetails.nickname)
        } catch (e: ClassCastException) {
            Optional.empty()
        }
    }
}
