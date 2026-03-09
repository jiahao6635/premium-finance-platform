package com.company.platform.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.platform.rbac.entity.UserEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
    UserEntity selectByUsername(@Param("username") String username);

    @Select("""
        SELECT DISTINCT p.permission_code
        FROM sys_permission p
        JOIN sys_role_permission rp ON p.id = rp.permission_id
        JOIN sys_user_role ur ON ur.role_id = rp.role_id
        WHERE ur.user_id = #{userId} AND p.status = 1
        """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
