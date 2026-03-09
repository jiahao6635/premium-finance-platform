package com.company.platform.rbac.security;

import com.company.platform.common.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 鉴权过滤器，在请求进入业务控制器前完成令牌校验与认证上下文注入。
 * <p>边界：仅处理 access token，不处理 refresh token 的换发逻辑。</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final PlatformUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
        JwtTokenService jwtTokenService,
        PlatformUserDetailsService userDetailsService,
        TokenBlacklistService tokenBlacklistService,
        ObjectMapper objectMapper
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行 JWT 解析与安全上下文设置。
     *
     * @param request 当前请求
     * @param response 当前响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 过滤异常
     * @throws IOException I/O 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String bearer = request.getHeader("Authorization");
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7);
                String tokenType = jwtTokenService.getTokenType(token);
                // 仅接受 access token 进入资源访问链路，refresh token 不可用于接口访问。
                if (JwtTokenService.TOKEN_TYPE_ACCESS.equals(tokenType)) {
                    String tokenId = jwtTokenService.getTokenId(token);
                    // 黑名单命中表示已注销或被风控吊销，禁止继续认证。
                    if (!tokenBlacklistService.isBlacklisted(tokenId)) {
                        String username = jwtTokenService.getUsername(token);
                        PlatformUserDetails details = (PlatformUserDetails) userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            details,
                            null,
                            details.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), ApiResponse.failure("UNAUTHORIZED", ex.getMessage()));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Set<String> whitePrefixes = Set.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator"
        );
        return whitePrefixes.stream().anyMatch(uri::startsWith);
    }
}
