package com.example.manager;

import com.example.manager.entity.Device;
import com.example.manager.entity.UserHome;
import com.example.manager.service.DevicePermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 访客权限功能测试类
 * 测试基于角色的设备访问权限控制，特别是访客权限限制
 */
@SpringBootTest
@ActiveProfiles("test")
public class GuestPermissionTest {
    
    @Autowired
    private DevicePermissionService devicePermissionService;
    
    @Test
    public void testGuestPermissionRestrictions() {
        // 测试数据
        Long guestUserId = 3L; // 假设用户ID 3 是访客
        Long homeId = 1L;
        Long deviceId = 1L; // 灯设备
        Long operationId = 1L; // 打开操作
        
        // 测试访客权限检查
        boolean hasPermission = devicePermissionService.checkDevicePermission(guestUserId, homeId, deviceId, operationId);
        
        // 访客应该没有权限控制灯设备
        assertFalse(hasPermission, "访客不应该有权限控制灯设备");
    }
    
    @Test
    public void testGuestAccessibleDevices() {
        // 测试数据
        Long guestUserId = 3L;
        Long homeId = 1L;
        
        // 获取访客可访问的设备列表
        List<Device> accessibleDevices = devicePermissionService.getAccessibleDevices(guestUserId, homeId);
        
        // 验证访客只能访问特定类型的设备
        for (Device device : accessibleDevices) {
            List<Long> guestAccessibleTypes = devicePermissionService.getGuestAccessibleDeviceTypes();
            assertTrue(guestAccessibleTypes.contains(device.getTypeId()), 
                "访客只能访问特定类型的设备: " + device.getTypeId());
        }
    }
    
    @Test
    public void testUserRoleDetection() {
        // 测试数据
        Long guestUserId = 3L;
        Long memberUserId = 2L;
        Long hostUserId = 1L;
        Long homeId = 1L;
        
        // 测试角色检测
        UserHome.Role guestRole = devicePermissionService.getUserRole(guestUserId, homeId);
        UserHome.Role memberRole = devicePermissionService.getUserRole(memberUserId, homeId);
        UserHome.Role hostRole = devicePermissionService.getUserRole(hostUserId, homeId);
        
        // 验证角色检测
        assertEquals(UserHome.Role.GUEST, guestRole, "用户应该是访客角色");
        assertEquals(UserHome.Role.MEMBER, memberRole, "用户应该是家庭成员角色");
        assertEquals(UserHome.Role.HOST, hostRole, "用户应该是房主角色");
    }
    
    @Test
    public void testGuestUserIdentification() {
        // 测试数据
        Long guestUserId = 3L;
        Long memberUserId = 2L;
        Long homeId = 1L;
        
        // 测试访客用户识别
        boolean isGuest1 = devicePermissionService.isGuestUser(guestUserId, homeId);
        boolean isGuest2 = devicePermissionService.isGuestUser(memberUserId, homeId);
        
        // 验证访客识别
        assertTrue(isGuest1, "用户应该是访客");
        assertFalse(isGuest2, "用户不应该是访客");
    }
    
    @Test
    public void testGuestAccessibleDeviceTypes() {
        // 获取访客可访问的设备类型
        List<Long> accessibleTypes = devicePermissionService.getGuestAccessibleDeviceTypes();
        
        // 验证访客可访问的设备类型
        assertNotNull(accessibleTypes, "访客可访问设备类型列表不应为空");
        assertTrue(accessibleTypes.contains(5L), "访客应该可以访问窗户设备");
        assertTrue(accessibleTypes.contains(6L), "访客应该可以访问监控摄像头设备");
        assertFalse(accessibleTypes.contains(1L), "访客不应该可以访问灯设备");
    }
    
    @Test
    public void testHostFullPermission() {
        // 测试数据
        Long hostUserId = 1L;
        Long homeId = 1L;
        Long deviceId = 1L;
        Long operationId = 1L;
        
        // 测试房主权限
        boolean hasPermission = devicePermissionService.checkDevicePermission(hostUserId, homeId, deviceId, operationId);
        
        // 房主应该有完整权限
        assertTrue(hasPermission, "房主应该有完整权限");
    }
    
    @Test
    public void testMemberPermission() {
        // 测试数据
        Long memberUserId = 2L;
        Long homeId = 1L;
        Long deviceId = 1L;
        Long operationId = 1L;
        
        // 测试家庭成员权限
        boolean hasPermission = devicePermissionService.checkDevicePermission(memberUserId, homeId, deviceId, operationId);
        
        // 家庭成员应该有大部分权限
        assertTrue(hasPermission, "家庭成员应该有大部分权限");
    }
}
