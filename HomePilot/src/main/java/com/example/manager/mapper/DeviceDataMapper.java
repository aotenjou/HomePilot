package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.manager.entity.DeviceData_Plantcare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData_Plantcare> {
    void insertDeviceData(@Param("deviceId") Long deviceId, @Param("humidity") Integer humidity, @Param("illumination") Integer illumination, @Param("dataTime") LocalDateTime dataTime);
}
