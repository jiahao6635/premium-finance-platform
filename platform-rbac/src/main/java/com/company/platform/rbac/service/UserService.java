package com.company.platform.rbac.service;

import com.company.platform.rbac.entity.UserEntity;
import com.company.platform.rbac.model.dto.UserUpsertRequest;
import java.util.List;

public interface UserService {
    List<UserEntity> list();

    UserEntity getById(Long id);

    UserEntity getByUsername(String username);

    UserEntity create(UserUpsertRequest request);

    UserEntity update(Long id, UserUpsertRequest request);

    void delete(Long id);

    void bindRoles(Long userId, List<Long> roleIds);

    List<String> listPermissionCodes(Long userId);
}
