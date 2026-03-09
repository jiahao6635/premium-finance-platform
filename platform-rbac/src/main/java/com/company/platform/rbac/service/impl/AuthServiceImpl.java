package com.company.platform.rbac.service.impl;

import com.company.platform.common.exception.BusinessException;
import com.company.platform.common.exception.ErrorCode;
import com.company.platform.rbac.model.dto.LoginRequest;
import com.company.platform.rbac.model.dto.TokenResponse;
import com.company.platform.rbac.security.JwtTokenService;
import com.company.platform.rbac.security.PlatformUserDetails;
import com.company.platform.rbac.security.PlatformUserDetailsService;
import com.company.platform.rbac.security.TokenBlacklistService;
import com.company.platform.rbac.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PlatformUserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthServiceImpl(
        AuthenticationManager authenticationManager,
        PlatformUserDetailsService userDetailsService,
        JwtTokenService jwtTokenService,
        TokenBlacklistService tokenBlacklistService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        PlatformUserDetails principal = (PlatformUserDetails) authentication.getPrincipal();
        return new TokenResponse(
            jwtTokenService.generateAccessToken(principal),
            jwtTokenService.generateRefreshToken(principal)
        );
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        String tokenType = jwtTokenService.getTokenType(refreshToken);
        if (!JwtTokenService.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String tokenId = jwtTokenService.getTokenId(refreshToken);
        if (tokenBlacklistService.isBlacklisted(tokenId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String username = jwtTokenService.getUsername(refreshToken);
        PlatformUserDetails principal = (PlatformUserDetails) userDetailsService.loadUserByUsername(username);
        return new TokenResponse(
            jwtTokenService.generateAccessToken(principal),
            jwtTokenService.generateRefreshToken(principal)
        );
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            return;
        }
        String token = bearerToken.substring(7);
        String tokenId = jwtTokenService.getTokenId(token);
        long ttl = jwtTokenService.getRemainingSeconds(token);
        tokenBlacklistService.blacklist(tokenId, ttl);
    }
}
