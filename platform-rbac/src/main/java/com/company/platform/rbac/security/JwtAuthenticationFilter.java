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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String bearer = request.getHeader("Authorization");
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7);
                String tokenType = jwtTokenService.getTokenType(token);
                if (JwtTokenService.TOKEN_TYPE_ACCESS.equals(tokenType)) {
                    String tokenId = jwtTokenService.getTokenId(token);
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
