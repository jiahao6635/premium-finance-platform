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

/**
 * 认证服务实现，负责登录、刷新令牌与登出。
 * <p>边界：仅处理认证流程编排，不直接访问数据库；依赖 Spring Security 认证管理器、JWT 服务和 Redis 黑名单服务。</p>
 */
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

    /**
     * 使用用户名和密码完成认证并签发访问令牌/刷新令牌。
     *
     * @param request 登录请求，包含用户名与明文密码
     * @return 包含 access token 与 refresh token 的响应对象
     * @throws org.springframework.security.core.AuthenticationException 当用户名或密码错误时抛出
     */
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

    /**
     * 校验刷新令牌并重新签发新的一对令牌。
     *
     * @param refreshToken 客户端传入的刷新令牌
     * @return 新签发的访问令牌与刷新令牌
     * @throws BusinessException 当令牌类型非法、令牌已失效或命中黑名单时抛出未授权异常
     */
    @Override
    public TokenResponse refresh(String refreshToken) {
        String tokenType = jwtTokenService.getTokenType(refreshToken);
        // 仅允许使用 refresh token 换取新令牌，防止 access token 越权刷新。
        if (!JwtTokenService.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String tokenId = jwtTokenService.getTokenId(refreshToken);
        // 黑名单用于实现主动注销与风控失效，命中即拒绝。
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

    /**
     * 注销当前登录令牌，将其 tokenId 写入黑名单直到自然过期。
     *
     * @param bearerToken Authorization 请求头，格式为 {@code Bearer <token>}
     */
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
