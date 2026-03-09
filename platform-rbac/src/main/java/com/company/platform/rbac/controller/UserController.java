package com.company.platform.rbac.controller;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.rbac.entity.UserEntity;
import com.company.platform.rbac.model.dto.UserRoleBindRequest;
import com.company.platform.rbac.model.dto.UserUpsertRequest;
import com.company.platform.rbac.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器。
 * <p>边界：负责用户 CRUD 与角色绑定接口暴露，权限判定依赖 Spring Security + RBAC 权限表达式。</p>
 */
@RestController
@RequestMapping("/api/rbac/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询用户列表。
     *
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<List<UserEntity>> list() {
        // 仅具备 user:read 权限的主体可访问。
        return ApiResponse.success(userService.list());
    }

    /**
     * 创建用户。
     *
     * @param request 用户新增参数
     * @return 新建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<UserEntity> create(@RequestBody @Valid UserUpsertRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    /**
     * 更新用户信息。
     *
     * @param id 用户 ID
     * @param request 用户更新参数
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<UserEntity> update(@PathVariable Long id, @RequestBody @Valid UserUpsertRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    /**
     * 删除用户。
     *
     * @param id 用户 ID
     * @return 空成功响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 绑定用户角色。
     *
     * @param id 用户 ID
     * @param request 角色 ID 列表
     * @return 空成功响应
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<Void> bindRoles(@PathVariable Long id, @RequestBody UserRoleBindRequest request) {
        userService.bindRoles(id, request.getRoleIds());
        return ApiResponse.success();
    }
}
