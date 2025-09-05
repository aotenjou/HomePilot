package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Mqttdata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface MqttdataMapper extends BaseMapper<Mqttdata> {
    // 向后兼容的简单插入方法
    void InsertMqttdata(@Param("device_id") Long device_id,  @Param("datatime") LocalDateTime datatime,@Param("datatopic") String datatopic, @Param("datavalue") Integer datavalue);

    // 完整的传感器数据插入方法
    void insertSensorData(@Param("device_id") Long device_id,
                         @Param("datatime") LocalDateTime datatime,
                         @Param("datatopic") String datatopic,
                         @Param("temperature") Double temperature,
                         @Param("humidity") Double humidity,
                         @Param("light_a") Integer light_a,
                         @Param("light_b") Integer light_b,
                         @Param("light_c") Integer light_c,
                         @Param("fan_state") Integer fan_state,
                         @Param("fire_state") Integer fire_state,
                         @Param("gas_state") Integer gas_state);

    List<Mqttdata> getDeviceData(@Param("device_id") Long device_id);
}