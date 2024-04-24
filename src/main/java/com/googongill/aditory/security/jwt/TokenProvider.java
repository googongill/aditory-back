package com.googongill.aditory.security.jwt;

import com.googongill.aditory.domain.enums.Role;
import com.googongill.aditory.exception.UserException;
import com.googongill.aditory.security.jwt.dto.JwtDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

import static com.googongill.aditory.common.code.UserErrorCode.*;

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

    public static JwtDto createTokens(Long userId, String username, Role role) {
        // access-token 발급
        String accessToken = createAccessToken(userId, username, role);
        // refresh-token 발급
        String refreshToken = createRefreshToken();

        return new JwtDto(accessToken, refreshToken);
    }

    private static String createAccessToken(Long userId, String username, Role role) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

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
            throw new UserException(TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new UserException(TOKEN_INVALID);
        }
    }

    public static String getUsername(String accessToken) {
        try {
            return parseClaims(accessToken).get("username", String.class);
        } catch (IllegalArgumentException e) {
            throw new UserException(TOKEN_NOT_CONTAINS_USERNAME);
        }
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails) {
        if (userDetails == null) {
            log.error("userDetails == null");
        }
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public static void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new UserException(TOKEN_NOT_FOUND);
        } catch (ExpiredJwtException e) {
            throw new UserException(TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new UserException(TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new UserException(TOKEN_NOT_FOUND);
        }
    }
}
