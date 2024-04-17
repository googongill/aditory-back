package com.googongill.aditory.security.jwt;

import com.googongill.aditory.security.jwt.auth.PrincipalDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("authorization: {}", authorization);

            // token 미포함 시
            if (authorization == null) {
                log.warn("token not included");
                filterChain.doFilter(request, response);
                return;
            }

            // token 접두사 "Bearer " 누락
            if (!authorization.startsWith("Bearer ")) {
                log.error("invalid token: wrong token prefix");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "SignatureException error");
                return;
            }

            // 접두사 제외한 실제 token
            String token = TokenProvider.resolveToken(authorization);

            if (TokenProvider.getUsername(token) == null) {
                log.error("invalid token: username is null");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "SignatureException error");
            }

            // username 으로 UserDetails 가져오기
            String username = TokenProvider.getUsername(token);
            UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

            // Authentication 발급
            UsernamePasswordAuthenticationToken authenticationToken = TokenProvider.getAuthentication(userDetails);
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 최종 인증 완료
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (SignatureException | MalformedJwtException e) {
            log.error("invalid token : malformed token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "SignatureException error");
        } catch (ExpiredJwtException e) {
            log.error("authorization expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "ExpiredJwtException error");
        } catch (Exception e) {
            log.error("error occurred while processing token validation");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "error occurred while processing token validation");
            SecurityContextHolder.clearContext();
        }
    }
}
