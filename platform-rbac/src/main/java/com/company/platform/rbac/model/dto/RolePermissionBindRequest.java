package com.company.platform.rbac.model.dto;

import java.util.List;

public class RolePermissionBindRequest {
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
