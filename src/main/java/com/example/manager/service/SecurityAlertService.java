package com.example.manager.service;

import java.util.Map;

public interface SecurityAlertService {
    
    /**
     * 处理火焰传感器警报
     * @param deviceId 设备ID
     * @param homeId 家庭ID
     * @param roomId 房间ID
     * @param value 传感器值
     */
    void handleFireAlert(Long deviceId, Long homeId, Long roomId, int value);
    
    /**
     * 处理可燃气体传感器警报
     * @param deviceId 设备ID
     * @param homeId 家庭ID
     * @param roomId 房间ID
     * @param value 传感器值
     */
    void handleGasAlert(Long deviceId, Long homeId, Long roomId, int value);
    
    /**
     * 获取当前安全状态
     * @param homeId 家庭ID
     * @return 安全状态信息
     */
    Map<String, Object> getSecurityStatus(Long homeId);
}
