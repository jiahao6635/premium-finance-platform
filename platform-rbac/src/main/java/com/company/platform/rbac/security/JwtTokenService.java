package com.company.platform.rbac.security;

import com.company.platform.rbac.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(PlatformUserDetails userDetails) {
        return generateToken(userDetails, TOKEN_TYPE_ACCESS, properties.getAccessTokenExpireSeconds());
    }

    public String generateRefreshToken(PlatformUserDetails userDetails) {
        return generateToken(userDetails, TOKEN_TYPE_REFRESH, properties.getRefreshTokenExpireSeconds());
    }

    private String generateToken(PlatformUserDetails userDetails, String tokenType, long expiresInSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
            .issuer(properties.getIssuer())
            .subject(userDetails.getUsername())
            .claims(Map.of("uid", userDetails.getUserId(), "typ", tokenType))
            .id(UUID.randomUUID().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String getTokenId(String token) {
        return parseClaims(token).getId();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("typ", String.class);
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public long getRemainingSeconds(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(remaining, 0);
    }
}
