package com.example.manager.service.serviceImpl;

import com.example.manager.entity.Device;
import com.example.manager.entity.UserCustomPermission;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.RoleDefaultPermissionMapper;
import com.example.manager.mapper.UserCustomPermissionMapper;
import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.DevicePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设备权限服务实现类
 * 实现基于角色的设备访问权限控制，特别针对访客权限限制
 */
@Service
public class DevicePermissionServiceImpl implements DevicePermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DevicePermissionServiceImpl.class);
    
    @Autowired
    private UserCustomPermissionMapper userCustomPermissionMapper;
    
    @Autowired
    private RoleDefaultPermissionMapper roleDefaultPermissionMapper;
    
    @Autowired
    private UserHomeMapper userHomeMapper;
    
    @Autowired
    private DeviceMapper deviceMapper;
    
    // 访客可访问的设备类型（只读设备，如传感器、监控等）
    private static final List<Long> GUEST_ACCESSIBLE_DEVICE_TYPES = Arrays.asList(
        5L,  // 窗户（只能查看状态）
        6L   // 监控摄像头（只能查看，不能控制）
    );
    
    @Override
    public boolean checkDevicePermission(Long userId, Long homeId, Long deviceId, Long operationId) {
        logger.info("检查用户 {} 在家庭 {} 中对设备 {} 操作 {} 的权限", userId, homeId, deviceId, operationId);
        
        // 1. 首先检查用户自定义权限
        UserCustomPermission customPermission = userCustomPermissionMapper.selectPermission(userId, deviceId, operationId);
        if (customPermission != null) {
            boolean hasPermission = customPermission.getHasPermission();
            logger.info("用户自定义权限: {}", hasPermission);
            return hasPermission;
        }
        
        // 2. 获取用户在家庭中的角色
        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            logger.warn("用户 {} 在家庭 {} 中没有角色信息", userId, homeId);
            return false;
        }
        
        UserHome.Role userRole = UserHome.Role.values()[roleCode];
        logger.info("用户角色: {}", userRole.getDescription());
        
        // 3. 检查角色默认权限
        boolean hasDefaultPermission = roleDefaultPermissionMapper.selectPermission(roleCode, deviceId, operationId);
        logger.info("角色默认权限: {}", hasDefaultPermission);
        
        // 4. 特殊处理访客权限
        if (userRole == UserHome.Role.GUEST) {
            return checkGuestPermission(deviceId, operationId, hasDefaultPermission);
        }
        
        return hasDefaultPermission;
    }
    
    /**
     * 检查访客权限的特殊逻辑
     */
    private boolean checkGuestPermission(Long deviceId, Long operationId, boolean hasDefaultPermission) {
        // 获取设备信息
        List<Device> device = deviceMapper.selectById(deviceId);
        if (device == null) {
            logger.warn("设备 {} 不存在", deviceId);
            return false;
        }
        
//        Long deviceTypeId = device.getTypeId();
        List<Long> deviceTypeId= new ArrayList<>();
        for(Device devices: device){
            deviceTypeId.add(devices.getTypeId());
        }
        
        // 访客只能访问特定类型的设备
        if (!GUEST_ACCESSIBLE_DEVICE_TYPES.containsAll(deviceTypeId)) {
            logger.info("访客不能访问设备类型 {}", deviceTypeId);
            return false;
        }
        
        // 访客只能进行查看类操作（这里假设操作ID 1,2 是查看类操作）
        if (operationId > 2) {
            logger.info("访客不能执行操作 {}", operationId);
            return false;
        }
        
        return hasDefaultPermission;
    }
    
    @Override
    public List<Device> getAccessibleDevices(Long userId, Long homeId) {
        logger.info("获取用户 {} 在家庭 {} 中可访问的设备列表", userId, homeId);
        
        List<Device> allDevices = deviceMapper.selectDevicesByHomeId(homeId);
        List<Device> accessibleDevices = new ArrayList<>();
        
        // 获取用户角色
        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            logger.warn("用户 {} 在家庭 {} 中没有角色信息", userId, homeId);
            return accessibleDevices;
        }
        
        UserHome.Role userRole = UserHome.Role.values()[roleCode];
        
        for (Device device : allDevices) {
            // 对于访客，只返回可访问的设备类型
            if (userRole == UserHome.Role.GUEST) {
                if (GUEST_ACCESSIBLE_DEVICE_TYPES.contains(device.getTypeId())) {
                    accessibleDevices.add(device);
                }
            } else {
                // 房主和家庭成员可以访问所有设备
                accessibleDevices.add(device);
            }
        }
        
        logger.info("用户可访问设备数量: {}", accessibleDevices.size());
        return accessibleDevices;
    }
    
    @Override
    public UserHome.Role getUserRole(Long userId, Long homeId) {
        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            return null;
        }
        return UserHome.Role.values()[roleCode];
    }
    
    @Override
    public boolean isGuestUser(Long userId, Long homeId) {
        UserHome.Role role = getUserRole(userId, homeId);
        return role == UserHome.Role.GUEST;
    }
    
    @Override
    public List<Long> getGuestAccessibleDeviceTypes() {
        return new ArrayList<>(GUEST_ACCESSIBLE_DEVICE_TYPES);
    }
}
