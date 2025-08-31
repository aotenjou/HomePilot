package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Value;

@Data
@TableName("Device_Test")
public class DeviceTest {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("topic")
    private String topic;
}