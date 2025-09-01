package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.RoleDefaultPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleDefaultPermissionMapper extends BaseMapper<RoleDefaultPermissionMapper> {
    Boolean selectPermission(@Param("role") Integer Role, @Param("deviceId") Long deviceId, @Param("operationId") Long operationId);
}
