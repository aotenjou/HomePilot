package com.example.manager.service;

import com.example.manager.entity.DeviceData_Plantcare;

import java.util.List;

public interface DeviceDataService {
    /**
     * 根据设备ID获取最新的植物数据
     * @param deviceId 设备ID
     * @return 最新的植物数据
     */
    DeviceData_Plantcare getLatestPlantData(Long deviceId);

    /**
     * 根据设备ID获取最近的植物数据列表
     * @param deviceId 设备ID
     * @param limit 返回的数据条数限制
     * @return 植物数据列表
     */
    List<DeviceData_Plantcare> getRecentPlantData(Long deviceId, int limit);
}
