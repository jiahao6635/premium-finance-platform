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

@RestController
@RequestMapping("/api/rbac/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<List<UserEntity>> list() {
        return ApiResponse.success(userService.list());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<UserEntity> create(@RequestBody @Valid UserUpsertRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<UserEntity> update(@PathVariable Long id, @RequestBody @Valid UserUpsertRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:write')")
    public ApiResponse<Void> bindRoles(@PathVariable Long id, @RequestBody UserRoleBindRequest request) {
        userService.bindRoles(id, request.getRoleIds());
        return ApiResponse.success();
    }
}
