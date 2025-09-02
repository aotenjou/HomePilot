package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.TableField;

public class DeviceData_Plantcare{

    @TableField("humidity")
    private Integer humidity;

    @TableField("illumination")
    private Integer illumination;

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getIllumination() {
        return illumination;
    }

    public void setIllumination(Integer illumination) {
        this.illumination = illumination;
    }
}
