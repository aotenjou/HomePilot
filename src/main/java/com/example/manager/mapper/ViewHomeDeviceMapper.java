package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.ViewHomeDevice;

import java.util.List;

public interface ViewHomeDeviceMapper extends BaseMapper<ViewHomeDevice> {
    List<ViewHomeDevice> getHomeDevicesView(Long homeId);
}
