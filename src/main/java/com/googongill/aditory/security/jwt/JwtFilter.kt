package com.googongill.aditory.security.jwt

import com.googongill.aditory.common.code.CommonErrorCode
import com.googongill.aditory.exception.BusinessException
import com.googongill.aditory.security.jwt.user.PrincipalDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtFilter : OncePerRequestFilter() {
    private val tokenProvider: TokenProvider? = null
    private val principalDetailsService: PrincipalDetailsService? = null
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (authorization != null) {
                // 접두사 제외한 실제 token
                val token = tokenProvider!!.resolveToken(authorization)
                // 토큰 검증
                tokenProvider.validateToken(token)
                // 토큰에서 username 추출
                val username = tokenProvider.getUsername(token)
                val userDetails = principalDetailsService!!.loadUserByUsername(username)
                // authentication 객체 생성, UserDetails 담기
                val authentication = tokenProvider.getAuthentication(userDetails)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            throw BusinessException(CommonErrorCode.AUTHENTICATE_JWT_FAIL)
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response)
    }
}
