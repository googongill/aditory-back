package com.googongill.aditory.security.jwt;

import com.googongill.aditory.exception.BusinessException;
import com.googongill.aditory.security.jwt.user.PrincipalDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.googongill.aditory.common.code.CommonErrorCode.AUTHENTICATE_JWT_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("token: {}", authorization);

            if (authorization != null) {
                String token = TokenProvider.resolveToken(authorization);
                tokenProvider.validateToken(token);
                String username = tokenProvider.getUsername(token);
                UserDetails userDetails = principalDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthentication(userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Spring Security doFilter 중에 발생한 에러: {}", e);
            throw new BusinessException(AUTHENTICATE_JWT_FAIL);
        }

        filterChain.doFilter(request, response);
    }
}
