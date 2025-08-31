package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.TableField;

public class DeviceData_Plantcare{

    @TableField("humidity")
    private Integer humidity;

    @TableField("illumination")
    private Integer illumination;
}
