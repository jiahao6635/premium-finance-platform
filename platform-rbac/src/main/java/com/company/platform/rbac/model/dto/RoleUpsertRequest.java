package com.company.platform.rbac.model.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleUpsertRequest {
    @NotBlank
    private String roleCode;
    @NotBlank
    private String roleName;
    private Integer status = 1;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
