package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.UserHome;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserHomeMapper extends BaseMapper<UserHome> {
    Integer selectRoleByUserIdAndHomeId(@Param("userId") Long userId, @Param("homeId") Long homeId);

    UserHome checkUserIdAndHomeId(@Param("userId") Long userId, @Param("homeId") List<Long> homeId);

    void createUserHome(@Param("userId") Long userId, @Param("homeId") Long homeId, @Param("role") Integer role);

    void deleteUserHomeByHomeId(@Param("homeId") Long homeId);

    List<Long> selectUserIdByHomeId(@Param("homeId") Long homeId);

    List<Long> selectHomeIdByUserId(@Param("userId") Long userId);

    List<UserHome> selectUserHomeByUserId(@Param("userId") Long userId);

    Integer countByUserIdAndHomeId(@Param("userId") Long userId, @Param("homeId") Long homeId);

    void insertUserHome(@Param("userId") Long userId, @Param("homeId") Long homeId, @Param("role") Integer role);
}
