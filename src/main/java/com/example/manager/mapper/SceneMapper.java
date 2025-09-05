package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Scene;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SceneMapper extends BaseMapper<Scene> {
    void insertScene(@Param("userId") Long userId, @Param("homeId") Long homeId, @Param("name") String name, @Param("description") String description, @Param("status") Integer status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Long selectLastInsertId();

    Scene selectActiveById(@Param("id") Long id);

    void deleteSceneById(@Param("id") Long id);

    List<Scene> selectByHomeId(@Param("homeId") Long homeId);

    void updateScene(@Param("scene") Scene scene);
}