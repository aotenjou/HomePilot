package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Mqttdata;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface MqttdataMapper extends BaseMapper<Mqttdata> {
    void InsertMqttdata(@Param("device_id") Long device_id,  @Param("datatime") LocalDateTime datatime,@Param("datatopic") String datatopic, @Param("datavalue") Integer datavalue);

    List<Mqttdata> getDeviceData(@Param("device_id") Long device_id);
}