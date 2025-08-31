package com.example.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceType;


import java.time.LocalDateTime;
import java.util.List;

public interface DeviceManageService extends IService<Device> {
    // 添加设备
    Long addDevice(Device device);
    // 获取家庭可用设备列表
    List<Device> getDevicesByHomeID(Long homeID);
    // 更新设备信息
    boolean updateDevice(Device device);
    // 逻辑删除设备
    boolean removeDevice(Long id);
    // 设备最后活跃时间
    boolean updateLastActiveTime(Long id, LocalDateTime time);
    // 有权限操作的设备
    List<Device> getDevicesByUserAndHome(Long userId, Long homeId);
    // 新增：查询设备类型
    List<DeviceType> getAllDeviceTypes();
}
