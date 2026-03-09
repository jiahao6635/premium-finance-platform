package com.company.platform.rbac.controller;

import com.company.platform.common.api.ApiResponse;
import com.company.platform.rbac.entity.PermissionEntity;
import com.company.platform.rbac.model.dto.PermissionUpsertRequest;
import com.company.platform.rbac.service.PermissionService;
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
@RequestMapping("/api/rbac/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('perm:read')")
    public ApiResponse<List<PermissionEntity>> list() {
        return ApiResponse.success(permissionService.list());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('perm:write')")
    public ApiResponse<PermissionEntity> create(@RequestBody @Valid PermissionUpsertRequest request) {
        return ApiResponse.success(permissionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perm:write')")
    public ApiResponse<PermissionEntity> update(@PathVariable Long id, @RequestBody @Valid PermissionUpsertRequest request) {
        return ApiResponse.success(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perm:write')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.success();
    }
}
