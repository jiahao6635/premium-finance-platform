package com.company.platform.rbac.model.dto;

import java.util.List;

public class UserRoleBindRequest {
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
