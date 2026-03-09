package com.company.platform.rbac.config;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.rbac.security.JwtAuthenticationFilter;
import com.company.platform.rbac.security.PlatformUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置。
 * <p>职责：定义密码编码器、认证管理器、无状态过滤器链以及认证/鉴权失败响应；
 * 依赖 JWT 过滤器与用户详情服务，不直接处理业务权限数据查询。</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    /**
     * 提供委派式密码编码器，兼容多种哈希算法前缀。
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 组装认证管理器，使用 DAO 模式基于用户信息和密码进行认证。
     *
     * @param userDetailsService 用户加载服务
     * @param passwordEncoder 密码编码器
     * @return 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(PlatformUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    /**
     * 配置无状态安全过滤器链。
     *
     * @param http HttpSecurity 配置入口
     * @param jwtAuthenticationFilter JWT 鉴权过滤器
     * @param objectMapper JSON 序列化器
     * @return SecurityFilterChain
     * @throws Exception 配置构建失败时抛出
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        ObjectMapper objectMapper
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/health"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, ex) -> writeJson(response, HttpStatus.UNAUTHORIZED, objectMapper,
                    ApiResponse.failure("UNAUTHORIZED", ex.getMessage())))
                .accessDeniedHandler((request, response, ex) -> writeJson(response, HttpStatus.FORBIDDEN, objectMapper,
                    ApiResponse.failure("FORBIDDEN", ex.getMessage())))
            )
            // JWT 过滤器必须位于用户名密码过滤器之前，确保请求先基于令牌建立认证上下文。
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, ObjectMapper objectMapper, Object body)
        throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
