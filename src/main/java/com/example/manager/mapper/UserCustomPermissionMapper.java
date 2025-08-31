package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.UserCustomPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCustomPermissionMapper extends BaseMapper<UserCustomPermission> {
    void createUserCustomPermission(UserCustomPermission userCustomPermission);

    boolean checkPermission(@Param("id") Long id);

    void deleteUserCustomPermission(@Param("id") Long id);

    boolean checkUserOperationPermission(@Param("userId") Long userId, @Param("operationId") Long operationId, @Param("deviceId") Long deviceId);

    UserCustomPermission selectPermission(@Param("userId") Long userId, @Param("deviceId") Long deviceId, @Param("operationId") Long operationId);
}
