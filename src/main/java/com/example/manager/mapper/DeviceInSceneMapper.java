package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.OperationOfDevice;
import com.example.manager.entity.DeviceInScene;
import com.example.manager.entity.DeviceOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceInSceneMapper extends BaseMapper<DeviceInScene> {
    void createDeviceInScene(@Param("deviceOperation") List<OperationOfDevice> deviceOperation, @Param("sceneId") Long sceneId);

    void deleteDeviceInScene(@Param("sceneId") Long sceneId);

    List<DeviceInScene> selectBySceneId(@Param("sceneId") Long sceneId);

    void deleteDeviceNotInScene(@Param("sceneId") Long sceneId, @Param("deviceOperation") List<OperationOfDevice> deviceOperation);

    void updateDeviceInScene(@Param("sceneId") Long sceneId, @Param("deviceOperation") List<OperationOfDevice> deviceOperation);
}
