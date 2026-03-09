package com.company.platform.rbac.service;

import com.company.platform.rbac.entity.RoleEntity;
import com.company.platform.rbac.model.dto.RoleUpsertRequest;
import java.util.List;

public interface RoleService {
    List<RoleEntity> list();

    RoleEntity getById(Long id);

    RoleEntity create(RoleUpsertRequest request);

    RoleEntity update(Long id, RoleUpsertRequest request);

    void delete(Long id);

    void bindPermissions(Long roleId, List<Long> permissionIds);
}
