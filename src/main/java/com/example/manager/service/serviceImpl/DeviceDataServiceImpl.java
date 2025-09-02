package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.entity.DeviceData_Plantcare;
import com.example.manager.mapper.DeviceDataMapper;
import com.example.manager.service.DeviceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData_Plantcare> implements DeviceDataService {

    @Autowired
    private DeviceDataMapper deviceDataMapper;

    @Override
    public DeviceData_Plantcare getLatestPlantData(Long deviceId) {
        QueryWrapper<DeviceData_Plantcare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId)
                   .orderByDesc("data_time")
                   .last("LIMIT 1");
        return deviceDataMapper.selectOne(queryWrapper);
    }

    @Override
    public List<DeviceData_Plantcare> getRecentPlantData(Long deviceId, int limit) {
        QueryWrapper<DeviceData_Plantcare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId)
                   .orderByDesc("data_time")
                   .last("LIMIT " + limit);
        return deviceDataMapper.selectList(queryWrapper);
    }
}
