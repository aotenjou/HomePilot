package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Data
@TableName("Mqtt_data")
public class Mqttdata{


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("device_id")
    private Long deviceId;

    @TableField("datatime")
    private LocalDateTime dataTime;

    @TableField("datatopic")
    private String topic;

    @TableField("datavalue")
    private Integer DataValue;

    @TableField("sensor_data")
    private String sensorData; // JSON格式的完整传感器数据

    @TableField("temperature")
    private Double temperature;

    @TableField("humidity")
    private Double humidity;

    @TableField("light_a")
    private Integer lightA;

    @TableField("light_b")
    private Integer lightB;

    @TableField("light_c")
    private Integer lightC;

    @TableField("fan_state")
    private Integer fanState;

    @TableField("fire_state")
    private Integer fireState;

    @TableField("gas_state")
    private Integer gasState;
}