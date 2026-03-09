package com.company.platform.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.platform.common.exception.BusinessException;
import com.company.platform.common.exception.ErrorCode;
import com.company.platform.rbac.entity.UserEntity;
import com.company.platform.rbac.entity.UserRoleEntity;
import com.company.platform.rbac.mapper.UserMapper;
import com.company.platform.rbac.mapper.UserRoleMapper;
import com.company.platform.rbac.model.dto.UserUpsertRequest;
import com.company.platform.rbac.service.UserService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> list() {
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public UserEntity getById(Long id) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public UserEntity create(UserUpsertRequest request) {
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "Username already exists");
        }
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setNickname(request.getNickname());
        entity.setStatus(request.getStatus());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(entity);

        bindRoles(entity.getId(), request.getRoleIds());
        return getById(entity.getId());
    }

    @Override
    @Transactional
    public UserEntity update(Long id, UserUpsertRequest request) {
        UserEntity entity = getById(id);
        entity.setNickname(request.getNickname());
        entity.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        bindRoles(id, request.getRoleIds());
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userMapper.deleteById(id);
        userRoleMapper.delete(new QueryWrapper<UserRoleEntity>().eq("user_id", id));
    }

    @Override
    @Transactional
    public void bindRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new QueryWrapper<UserRoleEntity>().eq("user_id", userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            UserRoleEntity relation = new UserRoleEntity();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            userRoleMapper.insert(relation);
        }
    }

    @Override
    public List<String> listPermissionCodes(Long userId) {
        List<String> permissionCodes = userMapper.selectPermissionCodesByUserId(userId);
        return permissionCodes == null ? Collections.emptyList() : permissionCodes;
    }
}
