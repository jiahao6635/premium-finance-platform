package com.company.platform.rbac.model.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionUpsertRequest {
    @NotBlank
    private String permissionCode;
    @NotBlank
    private String permissionName;
    private Integer status = 1;

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
