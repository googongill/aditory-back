package com.googongill.aditory.security.jwt;

import com.googongill.aditory.security.jwt.auth.PrincipalDetails;
import com.googongill.aditory.security.jwt.dto.JwtDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private final String secret;
    private static Key secretKey;
    // access-token : 30 min
    private static final long accessTokenExpiredMs = 60 * 30;
    // refresh-token : 7 days (1 week)
    private static final long refreshTokenExpiredMs = 60 * 60 * 24 * 7;

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        this.secret = secret;

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public static JwtDto createTokens(Authentication authentication) {
        // access-token 발급
        String accessToken = createAccessToken(authentication);
        // refresh-token 발급
        String refreshToken = createRefreshToken();

        return new JwtDto(accessToken, refreshToken);
    }

    private static String createAccessToken(Authentication authentication) {
        // UserDetails 가져오기
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        // 권한 목록만 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims();
        claims.put("userId", userDetails.getUserId());
        claims.put("username", userDetails.getUsername());
        claims.put("role", authorities);

        return Jwts.builder()
                .setSubject("access-token")
                .setClaims(claims)
                .setIssuer("googongill")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private static String createRefreshToken() {
        return Jwts.builder()
                .setSubject("refresh-token")
                .setIssuer("googongill")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Token 에서 "Bearer " 제외한 실제 토큰 반환
    public static String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public static Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다.");
            // 추후에 Exception Refactoring
            return e.getClaims();
        }
    }

    public static String getUsername(String accessToken) {
        return parseClaims(accessToken).get("username", String.class);
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    // 사용할지는 추후에
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            Claims claims = parseClaims(token);
            log.info("userId: {}", claims.get("userId"));
            log.info("username: {}", claims.get("username"));
            log.info("role: {}", claims.get("role"));
            log.info("expiration: {}", claims.getExpiration());
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
