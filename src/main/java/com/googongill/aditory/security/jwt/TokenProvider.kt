package com.googongill.aditory.security.jwt

import com.googongill.aditory.common.code.UserErrorCode
import com.googongill.aditory.domain.enums.Role
import com.googongill.aditory.exception.UserException
import com.googongill.aditory.security.jwt.dto.JwtResult
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class TokenProvider(@Value("\${jwt.secret}") val secret: String) {

    val secretKey: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    companion object {
        // access-token : 30 min = 60 * 30
        private const val ACCESS_TOKEN_EXPIRED_MS = 60 * 10 * 1000L
        // refresh-token : 7 days (1 week) = 60 * 60 * 24 * 7
        private const val REFRESH_TOKEN_EXPIRED_MS = 60 * 60 * 6 * 1000L
    }

    fun createTokens(userId: Long, username: String, role: Role): JwtResult {
        // access-token 발급
        val accessToken = createAccessToken(userId, username, role)
        // refresh-token 발급
        val refreshToken = createRefreshToken()

        return JwtResult(accessToken, refreshToken)
    }

    private fun createAccessToken(userId: Long, username: String, role: Role): String {
        val claims = Jwts.claims().apply {
            put("userId", userId)
            put("username", username)
            put("role", role)
        }

        return Jwts.builder()
            .setSubject("access-token")
            .setClaims(claims)
            .setIssuer("googongill")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_MS * 1000))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun createAccessToken(username: String): String {
        val claims = Jwts.claims().apply {
            put("username", username)
        }

        return Jwts.builder()
            .setSubject("access-token")
            .setClaims(claims)
            .setIssuer("social")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_MS * 1000))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun createRefreshToken(): String {
        return Jwts.builder()
            .setSubject("refresh-token")
            .setIssuer("googongill")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_MS * 1000))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    // Token 에서 "Bearer " 제외한 실제 토큰 반환
    fun resolveToken(token: String): String {
        return if (!token.isNullOrBlank() && token.startsWith("Bearer ")) {
            token.substring(7)
        } else {
            throw UserException(UserErrorCode.TOKEN_INVALID)
        }
    }

    fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .body
        } catch (e: ExpiredJwtException) {
            throw UserException(UserErrorCode.TOKEN_EXPIRED)
        } catch (e: JwtException) {
            throw UserException(UserErrorCode.TOKEN_INVALID)
        }
    }

    fun getUsername(accessToken: String): String {
        return try {
            parseClaims(accessToken).get("username", String::class.java)
        } catch (e: IllegalArgumentException) {
            throw UserException(UserErrorCode.TOKEN_MISSING_USERNAME)
        }
    }

    fun getAuthentication(userDetails: UserDetails?): UsernamePasswordAuthenticationToken {
        if (userDetails == null) {
            throw IllegalArgumentException("UserDetails cannot be null")
        }
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails!!.authorities)
    }

    fun validateToken(token: String?) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        } catch (e: SecurityException) {
            throw UserException(UserErrorCode.TOKEN_NOT_FOUND)
        } catch (e: MalformedJwtException) {
            throw UserException(UserErrorCode.TOKEN_NOT_FOUND)
        } catch (e: ExpiredJwtException) {
            throw UserException(UserErrorCode.TOKEN_EXPIRED)
        } catch (e: UnsupportedJwtException) {
            throw UserException(UserErrorCode.TOKEN_UNSUPPORTED)
        } catch (e: IllegalArgumentException) {
            throw UserException(UserErrorCode.TOKEN_NOT_FOUND)
        }
    }

}
