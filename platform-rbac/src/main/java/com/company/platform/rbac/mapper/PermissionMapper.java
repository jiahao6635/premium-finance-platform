package com.company.platform.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.platform.rbac.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
}
