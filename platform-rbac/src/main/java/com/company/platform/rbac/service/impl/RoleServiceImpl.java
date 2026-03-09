package com.company.platform.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.platform.common.exception.BusinessException;
import com.company.platform.common.exception.ErrorCode;
import com.company.platform.rbac.entity.RoleEntity;
import com.company.platform.rbac.entity.RolePermissionEntity;
import com.company.platform.rbac.entity.UserRoleEntity;
import com.company.platform.rbac.mapper.RoleMapper;
import com.company.platform.rbac.mapper.RolePermissionMapper;
import com.company.platform.rbac.mapper.UserRoleMapper;
import com.company.platform.rbac.model.dto.RoleUpsertRequest;
import com.company.platform.rbac.service.RoleService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper, UserRoleMapper userRoleMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<RoleEntity> list() {
        return roleMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public RoleEntity getById(Long id) {
        RoleEntity entity = roleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return entity;
    }

    @Override
    public RoleEntity create(RoleUpsertRequest request) {
        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setStatus(request.getStatus());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(entity);
        return getById(entity.getId());
    }

    @Override
    public RoleEntity update(Long id, RoleUpsertRequest request) {
        RoleEntity entity = getById(id);
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setStatus(request.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(entity);
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new QueryWrapper<RolePermissionEntity>().eq("role_id", id));
        userRoleMapper.delete(new QueryWrapper<UserRoleEntity>().eq("role_id", id));
    }

    @Override
    @Transactional
    public void bindPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(new QueryWrapper<RolePermissionEntity>().eq("role_id", roleId));
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Long permissionId : permissionIds) {
            RolePermissionEntity relation = new RolePermissionEntity();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            rolePermissionMapper.insert(relation);
        }
    }
}
