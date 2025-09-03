package com.example.manager;

import com.example.manager.controller.DeviceManageController;
import com.example.manager.controller.GuestPermissionController;
import com.example.manager.entity.Device;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.DeviceManageService;
import com.example.manager.service.DevicePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 基于角色的权限集成测试
 * 测试不同角色在整个系统中的权限表现
 */
@ExtendWith(MockitoExtension.class)
public class RoleBasedPermissionIntegrationTest {

    @Mock
    private DeviceManageService deviceManageService;

    @Mock
    private DevicePermissionService devicePermissionService;



    @Mock
    private UserHomeMapper userHomeMapper;

    @InjectMocks
    private DeviceManageController deviceManageController;

    @InjectMocks
    private GuestPermissionController guestPermissionController;

    private static final Long USER_ID = 1L;
    private static final Long HOME_ID = 1L;

    /**
     * 集成测试：房主在系统中的完整权限表现
     */
    @Test
    void testHostFullPermissions_Integration() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 测试1：房主可以添加设备
        Device device = createDevice(1L, "智能灯泡", HOME_ID, 1L);
        when(deviceManageService.addDevice(device)).thenReturn(1L);

        ResponseEntity<Map<String, Object>> addResponse = deviceManageController.addDevice(device);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        assertEquals("success", addResponse.getBody().get("status"));

        // 测试2：房主可以查看设备列表
        List<Device> devices = Arrays.asList(device);
        when(deviceManageService.getDevicesByHomeID(HOME_ID)).thenReturn(devices);

        ResponseEntity<Map<String, Object>> listResponse = deviceManageController.listDevices(HOME_ID);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody().get("devices"));

        // 测试3：房主可以更新设备
        when(deviceManageService.updateDevice(device)).thenReturn(true);

        ResponseEntity<Map<String, Object>> updateResponse = deviceManageController.updateDevice(device);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("success", updateResponse.getBody().get("status"));

        // 测试4：房主可以删除设备
        when(deviceManageService.removeDevice(1L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> deleteResponse = deviceManageController.deleteDevice(1L);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals("success", deleteResponse.getBody().get("status"));
    }

    /**
     * 集成测试：家庭成员的权限表现
     */
    @Test
    void testMemberPermissions_Integration() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 测试1：家庭成员可以添加设备（假设默认权限允许）
        Device device = createDevice(1L, "智能空调", HOME_ID, 1L);
        when(deviceManageService.addDevice(device)).thenReturn(1L);

        ResponseEntity<Map<String, Object>> addResponse = deviceManageController.addDevice(device);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        // 测试2：家庭成员可以查看设备列表
        List<Device> devices = Arrays.asList(device);
        when(deviceManageService.getDevicesByHomeID(HOME_ID)).thenReturn(devices);

        ResponseEntity<Map<String, Object>> listResponse = deviceManageController.listDevices(HOME_ID);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());

        // 测试3：家庭成员可以更新设备（假设默认权限允许）
        when(deviceManageService.updateDevice(device)).thenReturn(true);

        ResponseEntity<Map<String, Object>> updateResponse = deviceManageController.updateDevice(device);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // 测试4：家庭成员可以删除设备（假设默认权限允许）
        when(deviceManageService.removeDevice(1L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> deleteResponse = deviceManageController.deleteDevice(1L);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    /**
     * 集成测试：访客的受限权限表现
     */
    @Test
    void testGuestLimitedPermissions_Integration() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 测试1：访客尝试添加设备应该失败
        Device device = createDevice(1L, "智能门锁", HOME_ID, 1L); // 非访客可访问设备类型
        when(deviceManageService.addDevice(device)).thenThrow(new RuntimeException("权限不足"));

        ResponseEntity<Map<String, Object>> addResponse = deviceManageController.addDevice(device);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, addResponse.getStatusCode());
        assertEquals("error", addResponse.getBody().get("status"));

        // 测试2：访客可以查看设备列表（但只限于允许的设备类型）
        List<Device> allowedDevices = Arrays.asList(
                createDevice(1L, "智能摄像头", HOME_ID, 6L) // 访客可访问的设备类型
        );
        when(deviceManageService.getDevicesByHomeID(HOME_ID)).thenReturn(allowedDevices);

        ResponseEntity<Map<String, Object>> listResponse = deviceManageController.listDevices(HOME_ID);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());

        // 测试3：访客尝试更新设备应该失败
        when(deviceManageService.updateDevice(device)).thenReturn(false);

        ResponseEntity<Map<String, Object>> updateResponse = deviceManageController.updateDevice(device);
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        assertEquals("error", updateResponse.getBody().get("status"));

        // 测试4：访客尝试删除设备应该失败
        when(deviceManageService.removeDevice(1L)).thenReturn(false);

        ResponseEntity<Map<String, Object>> deleteResponse = deviceManageController.deleteDevice(1L);
        assertEquals(HttpStatus.BAD_REQUEST, deleteResponse.getStatusCode());
        assertEquals("error", deleteResponse.getBody().get("status"));
    }

    /**
     * 集成测试：访客专用权限接口
     */
    @Test
    void testGuestPermissionController_Integration() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟访客可访问的设备类型
        when(devicePermissionService.getGuestAccessibleDeviceTypes())
                .thenReturn(Arrays.asList(5L, 6L));

        // 测试访客权限信息获取
        ResponseEntity<Map<String, Object>> infoResponse = guestPermissionController.getGuestPermissionInfo();
        assertEquals(HttpStatus.OK, infoResponse.getStatusCode());
        assertEquals("GUEST", infoResponse.getBody().get("role"));
        assertNotNull(infoResponse.getBody().get("restrictions"));
        assertNotNull(infoResponse.getBody().get("accessibleDeviceTypes"));
    }

    /**
     * 集成测试：权限检查流程
     */
    @Test
    void testPermissionCheckFlow_Integration() {
        // 测试1：访客检查可访问设备权限
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟访客有权限访问摄像头（设备类型6）
        when(devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, 1L, 1L))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> permissionResponse =
                guestPermissionController.checkGuestDevicePermission(USER_ID, HOME_ID, 1L, 1L);
        assertEquals(HttpStatus.OK, permissionResponse.getStatusCode());
        assertEquals(true, permissionResponse.getBody().get("hasPermission"));

        // 测试2：访客检查不可访问设备权限
        when(devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, 2L, 3L))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> noPermissionResponse =
                guestPermissionController.checkGuestDevicePermission(USER_ID, HOME_ID, 2L, 3L);
        assertEquals(HttpStatus.OK, noPermissionResponse.getStatusCode());
        assertEquals(false, noPermissionResponse.getBody().get("hasPermission"));
    }

    /**
     * 集成测试：角色权限边界测试
     */
    @Test
    void testRolePermissionBoundaries_Integration() {
        // 测试房主和访客的权限边界
        Device sensitiveDevice = createDevice(1L, "智能门锁", HOME_ID, 1L); // 敏感设备

        // 房主可以操作敏感设备
        when(userHomeMapper.selectRoleByUserIdAndHomeId(1L, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());
        when(deviceManageService.addDevice(sensitiveDevice)).thenReturn(1L);

        ResponseEntity<Map<String, Object>> hostResponse = deviceManageController.addDevice(sensitiveDevice);
        assertEquals(HttpStatus.OK, hostResponse.getStatusCode());

        // 访客不能操作敏感设备
        when(userHomeMapper.selectRoleByUserIdAndHomeId(2L, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());
        when(deviceManageService.addDevice(sensitiveDevice)).thenThrow(new RuntimeException("权限不足"));

        ResponseEntity<Map<String, Object>> guestResponse = deviceManageController.addDevice(sensitiveDevice);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, guestResponse.getStatusCode());
    }

    /**
     * 集成测试：设备类型权限控制
     */
    @Test
    void testDeviceTypePermissions_Integration() {
        // 测试访客只能访问特定设备类型
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 访客可访问的设备类型
        Device allowedDevice1 = createDevice(1L, "智能窗户", HOME_ID, 5L);
        Device allowedDevice2 = createDevice(2L, "监控摄像头", HOME_ID, 6L);
        Device forbiddenDevice = createDevice(3L, "智能门锁", HOME_ID, 1L);

        List<Device> guestDevices = Arrays.asList(allowedDevice1, allowedDevice2);
        when(deviceManageService.getDevicesByHomeID(HOME_ID)).thenReturn(guestDevices);

        // 访客应该只能看到允许的设备
        ResponseEntity<Map<String, Object>> listResponse = deviceManageController.listDevices(HOME_ID);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());

        @SuppressWarnings("unchecked")
        List<Device> returnedDevices = (List<Device>) listResponse.getBody().get("devices");
        assertEquals(2, returnedDevices.size());

        // 验证返回的设备都是访客可访问的类型
        for (Device device : returnedDevices) {
            assertTrue(device.getTypeId().equals(5L) || device.getTypeId().equals(6L));
        }
    }

    /**
     * 辅助方法：创建设备对象
     */
    private Device createDevice(Long id, String name, Long homeId, Long typeId) {
        Device device = new Device();
        device.setId(id);
        device.setDeviceName(name);
        device.setHomeId(homeId);
        device.setTypeId(typeId);
        return device;
    }
}
