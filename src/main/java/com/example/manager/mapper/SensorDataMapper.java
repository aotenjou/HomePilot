package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.SensorData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SensorDataMapper extends BaseMapper<SensorData> {
    
    /**
     * 获取设备最新的传感器数据
     */
    @Select("SELECT * FROM sensor_data WHERE device_id = #{deviceId} ORDER BY create_time DESC LIMIT 1")
    SensorData getLatestSensorData(@Param("deviceId") Long deviceId);
    
    /**
     * 获取设备的历史传感器数据
     */
    @Select("SELECT * FROM sensor_data WHERE device_id = #{deviceId} ORDER BY create_time DESC LIMIT #{limit}")
    List<SensorData> getSensorDataHistory(@Param("deviceId") Long deviceId, @Param("limit") int limit);
    
    /**
     * 根据时间范围获取传感器数据
     */
    @Select("SELECT * FROM sensor_data WHERE device_id = #{deviceId} AND create_time BETWEEN #{startTime} AND #{endTime} ORDER BY create_time DESC")
    List<SensorData> getSensorDataByTimeRange(@Param("deviceId") Long deviceId, 
                                             @Param("startTime") String startTime, 
                                             @Param("endTime") String endTime);
    
    /**
     * 获取家庭所有设备的最新传感器数据
     */
    @Select("SELECT sd.* FROM sensor_data sd " +
            "INNER JOIN device d ON sd.device_id = d.id " +
            "WHERE d.home_id = #{homeId} " +
            "AND sd.create_time = (SELECT MAX(create_time) FROM sensor_data WHERE device_id = sd.device_id) " +
            "ORDER BY sd.create_time DESC")
    List<SensorData> getHomeSensorData(@Param("homeId") Long homeId);
    
    /**
     * 根据MAC地址获取最新数据
     */
    @Select("SELECT * FROM sensor_data WHERE device_mac = #{deviceMac} ORDER BY create_time DESC LIMIT 1")
    SensorData getLatestSensorDataByMac(@Param("deviceMac") String deviceMac);
}

