package com.example.manager.service;

import com.example.manager.entity.Device;
import com.example.manager.entity.UserHome;

import java.util.List;

/**
 * 设备权限服务接口
 * 负责处理基于角色的设备访问权限控制
 */
public interface DevicePermissionService {
    
    /**
     * 检查用户是否有权限访问指定设备
     * @param userId 用户ID
     * @param homeId 家庭ID
     * @param deviceId 设备ID
     * @param operationId 操作ID
     * @return 是否有权限
     */
    boolean checkDevicePermission(Long userId, Long homeId, Long deviceId, Long operationId);
    
    /**
     * 获取用户在家庭中可访问的设备列表
     * @param userId 用户ID
     * @param homeId 家庭ID
     * @return 可访问的设备列表
     */
    List<Device> getAccessibleDevices(Long userId, Long homeId);
    
    /**
     * 获取用户在家庭中的角色
     * @param userId 用户ID
     * @param homeId 家庭ID
     * @return 用户角色
     */
    UserHome.Role getUserRole(Long userId, Long homeId);
    
    /**
     * 检查是否为访客用户
     * @param userId 用户ID
     * @param homeId 家庭ID
     * @return 是否为访客
     */
    boolean isGuestUser(Long userId, Long homeId);
    
    /**
     * 获取访客可访问的设备类型列表
     * @return 访客可访问的设备类型ID列表
     */
    List<Long> getGuestAccessibleDeviceTypes();
}
