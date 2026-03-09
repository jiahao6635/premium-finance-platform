package com.company.platform.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.platform.common.exception.BusinessException;
import com.company.platform.common.exception.ErrorCode;
import com.company.platform.rbac.entity.PermissionEntity;
import com.company.platform.rbac.mapper.PermissionMapper;
import com.company.platform.rbac.model.dto.PermissionUpsertRequest;
import com.company.platform.rbac.service.PermissionService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionEntity> list() {
        return permissionMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PermissionEntity getById(Long id) {
        PermissionEntity entity = permissionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return entity;
    }

    @Override
    public PermissionEntity create(PermissionUpsertRequest request) {
        PermissionEntity entity = new PermissionEntity();
        entity.setPermissionCode(request.getPermissionCode());
        entity.setPermissionName(request.getPermissionName());
        entity.setStatus(request.getStatus());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        permissionMapper.insert(entity);
        return getById(entity.getId());
    }

    @Override
    public PermissionEntity update(Long id, PermissionUpsertRequest request) {
        PermissionEntity entity = getById(id);
        entity.setPermissionCode(request.getPermissionCode());
        entity.setPermissionName(request.getPermissionName());
        entity.setStatus(request.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        permissionMapper.updateById(entity);
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteById(id);
    }
}
