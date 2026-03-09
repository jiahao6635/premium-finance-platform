package com.company.platform.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.platform.rbac.entity.RoleEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("""
        SELECT r.*
        FROM sys_role r
        JOIN sys_user_role ur ON ur.role_id = r.id
        WHERE ur.user_id = #{userId} AND r.status = 1
        """)
    List<RoleEntity> selectByUserId(@Param("userId") Long userId);
}
