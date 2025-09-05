package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sensor_data")
public class SensorData {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("device_id")
    private Long deviceId;
    
    @TableField("device_mac")
    private String deviceMac;
    
    @TableField("topic")
    private String topic;
    
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
    
    @TableField("auto_temp")
    private Integer autoTemp;
    
    @TableField("fire_open")
    private Integer fireOpen;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("message_raw")
    private String messageRaw;
}

