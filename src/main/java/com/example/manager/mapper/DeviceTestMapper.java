package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.DeviceTest;

import java.util.List;

public interface DeviceTestMapper extends BaseMapper<DeviceTest> {
    List<DeviceTest> selectAll();
}
