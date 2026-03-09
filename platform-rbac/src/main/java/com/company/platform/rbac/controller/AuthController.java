package com.company.platform.rbac.controller;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.rbac.model.dto.LoginRequest;
import com.company.platform.rbac.model.dto.TokenResponse;
import com.company.platform.rbac.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口控制器，提供登录、刷新与登出能力。
 * <p>边界：仅负责 HTTP 参数绑定与响应封装，认证细节下沉到 AuthService。</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户名密码登录。
     *
     * @param request 登录请求体
     * @return 包含 access/refresh token 的响应
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 使用刷新令牌换取新令牌。
     *
     * @param refreshToken 刷新令牌，请求头 X-Refresh-Token
     * @return 新的令牌对
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ApiResponse.success(authService.refresh(refreshToken));
    }

    /**
     * 注销当前会话。
     *
     * @param authorization Authorization 请求头，可为空
     * @return 空成功响应
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ApiResponse.success();
    }
}
