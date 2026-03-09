package com.company.platform.rbac.security;

import com.company.platform.rbac.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    @Test
    void shouldGenerateAndParseAccessToken() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("ThisIsATestSecretKeyThatHasEnoughLengthForHS256123456789");
        properties.setAccessTokenExpireSeconds(120);
        JwtTokenService tokenService = new JwtTokenService(properties);

        PlatformUserDetails userDetails = new PlatformUserDetails(1L, "admin", "pwd", Set.of("user:read"), true);
        String token = tokenService.generateAccessToken(userDetails);

        Claims claims = tokenService.parseClaims(token);
        assertEquals("admin", claims.getSubject());
        assertEquals("access", claims.get("typ", String.class));
        assertNotNull(claims.getId());
        assertTrue(tokenService.getRemainingSeconds(token) > 0);
    }

    @Test
    void shouldGenerateRefreshTokenWithExpectedType() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("ThisIsATestSecretKeyThatHasEnoughLengthForHS256123456789");
        JwtTokenService tokenService = new JwtTokenService(properties);

        PlatformUserDetails userDetails = new PlatformUserDetails(2L, "ops", "pwd", Set.of(), true);
        String token = tokenService.generateRefreshToken(userDetails);

        assertEquals("refresh", tokenService.getTokenType(token));
        assertEquals("ops", tokenService.getUsername(token));
    }
}
