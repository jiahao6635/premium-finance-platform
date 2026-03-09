package com.company.platform.rbac.controller;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.rbac.entity.RoleEntity;
import com.company.platform.rbac.model.dto.RolePermissionBindRequest;
import com.company.platform.rbac.model.dto.RoleUpsertRequest;
import com.company.platform.rbac.service.RoleService;
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
 * 角色管理控制器，提供角色维护和权限绑定接口。
 */
@RestController
@RequestMapping("/api/rbac/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 查询角色列表。
     *
     * @return 角色集合
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<List<RoleEntity>> list() {
        return ApiResponse.success(roleService.list());
    }

    /**
     * 创建角色。
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:write')")
    public ApiResponse<RoleEntity> create(@RequestBody @Valid RoleUpsertRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    /**
     * 更新角色。
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public ApiResponse<RoleEntity> update(@PathVariable Long id, @RequestBody @Valid RoleUpsertRequest request) {
        return ApiResponse.success(roleService.update(id, request));
    }

    /**
     * 删除角色。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:write')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 绑定角色权限。
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:write')")
    public ApiResponse<Void> bindPermissions(@PathVariable Long id, @RequestBody RolePermissionBindRequest request) {
        roleService.bindPermissions(id, request.getPermissionIds());
        return ApiResponse.success();
    }
}
