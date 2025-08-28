package com.example.manager.service;


import com.example.manager.entity.Mqttdata;

import java.util.List;
import java.util.Map;

public interface DeviceInteractService {
    void sendCommand(String deviceId, String operation);


    boolean checkDeviceOperationPermission(Long deviceId, Long OperationId, Long userId);


    boolean checkDeviceOnlineStatus(Long deviceId);

    boolean checkDeviceActiveStatus(Long deviceId);

    // 移动设备
    boolean moveDevice(Long deviceId, Long roomId);

    List<Mqttdata> getDeviceData(Long deviceId);
}
