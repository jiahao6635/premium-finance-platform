package com.company.platform.rbac.service;

import com.company.platform.rbac.entity.PermissionEntity;
import com.company.platform.rbac.model.dto.PermissionUpsertRequest;
import java.util.List;

public interface PermissionService {
    List<PermissionEntity> list();

    PermissionEntity getById(Long id);

    PermissionEntity create(PermissionUpsertRequest request);

    PermissionEntity update(Long id, PermissionUpsertRequest request);

    void delete(Long id);
}
