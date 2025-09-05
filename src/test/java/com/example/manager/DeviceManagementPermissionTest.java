package com.example.manager;

import com.example.manager.controller.DeviceManageController;
import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceType;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 设备管理权限测试
 * 测试不同角色在设备管理方面的权限差异
 */
@ExtendWith(MockitoExtension.class)
public class DeviceManagementPermissionTest {

    @Mock
    private DeviceManageService deviceService;



    @Mock
    private UserHomeMapper userHomeMapper;

    @InjectMocks
    private DeviceManageController deviceManageController;

    private static final Long USER_ID = 1L;
    private static final Long HOME_ID = 1L;
    private static final Long DEVICE_ID = 1L;

    /**
     * 测试房主添加设备权限 - 应该成功
     */
    @Test
    void testHostAddDevice_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟设备添加成功
        Device device = createDevice(DEVICE_ID, "智能灯泡", HOME_ID, 1L);
        when(deviceService.addDevice(device)).thenReturn(DEVICE_ID);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.addDevice(device);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("添加设备成功", response.getBody().get("message"));
        assertEquals(DEVICE_ID, response.getBody().get("deviceId"));
    }

    /**
     * 测试家庭成员添加设备权限 - 应该成功（假设默认权限允许）
     */
    @Test
    void testMemberAddDevice_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟设备添加成功
        Device device = createDevice(DEVICE_ID, "智能空调", HOME_ID, 1L);
        when(deviceService.addDevice(device)).thenReturn(DEVICE_ID);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.addDevice(device);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("添加设备成功", response.getBody().get("message"));
    }

    /**
     * 测试访客添加设备权限 - 应该被拒绝
     */
    @Test
    void testGuestAddDevice_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟设备添加失败（权限不足）
        Device device = createDevice(DEVICE_ID, "智能摄像头", HOME_ID, 6L);
        when(deviceService.addDevice(device)).thenThrow(new RuntimeException("权限不足"));

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.addDevice(device);

        // 验证结果 - 访客应该被拒绝添加设备
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertTrue(((String) response.getBody().get("message")).contains("权限不足"));
    }

    /**
     * 测试房主删除设备权限 - 应该成功
     */
    @Test
    void testHostDeleteDevice_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟设备存在且删除成功
        when(deviceService.removeDevice(DEVICE_ID)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.deleteDevice(DEVICE_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("删除成功", response.getBody().get("message"));
    }

    /**
     * 测试家庭成员删除设备权限 - 应该成功（假设默认权限允许）
     */
    @Test
    void testMemberDeleteDevice_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟设备存在且删除成功
        when(deviceService.removeDevice(DEVICE_ID)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.deleteDevice(DEVICE_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("删除成功", response.getBody().get("message"));
    }

    /**
     * 测试访客删除设备权限 - 应该被拒绝
     */
    @Test
    void testGuestDeleteDevice_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟删除失败（权限不足）
        when(deviceService.removeDevice(DEVICE_ID)).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.deleteDevice(DEVICE_ID);

        // 验证结果 - 访客应该被拒绝删除设备
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("删除失败", response.getBody().get("message"));
    }

    /**
     * 测试房主更新设备权限 - 应该成功
     */
    @Test
    void testHostUpdateDevice_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟设备更新成功
        Device device = createDevice(DEVICE_ID, "智能灯泡", HOME_ID, 1L);
        when(deviceService.updateDevice(device)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.updateDevice(device);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("设备信息更新成功", response.getBody().get("message"));
    }

    /**
     * 测试家庭成员更新设备权限 - 应该成功（假设默认权限允许）
     */
    @Test
    void testMemberUpdateDevice_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟设备更新成功
        Device device = createDevice(DEVICE_ID, "智能空调", HOME_ID, 1L);
        when(deviceService.updateDevice(device)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.updateDevice(device);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("设备信息更新成功", response.getBody().get("message"));
    }

    /**
     * 测试访客更新设备权限 - 应该被拒绝
     */
    @Test
    void testGuestUpdateDevice_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟设备更新失败（权限不足）
        Device device = createDevice(DEVICE_ID, "智能摄像头", HOME_ID, 6L);
        when(deviceService.updateDevice(device)).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.updateDevice(device);

        // 验证结果 - 访客应该被拒绝更新设备
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("更新失败", response.getBody().get("message"));
    }

    /**
     * 测试房主查看设备列表权限 - 应该成功
     */
    @Test
    void testHostListDevices_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟获取设备列表成功
        List<Device> devices = Arrays.asList(
                createDevice(1L, "智能灯泡", HOME_ID, 1L),
                createDevice(2L, "智能空调", HOME_ID, 1L)
        );
        when(deviceService.getDevicesByHomeID(HOME_ID)).thenReturn(devices);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.listDevices(HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("devices"));
    }

    /**
     * 测试家庭成员查看设备列表权限 - 应该成功
     */
    @Test
    void testMemberListDevices_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟获取设备列表成功
        List<Device> devices = Arrays.asList(
                createDevice(1L, "智能灯泡", HOME_ID, 1L)
        );
        when(deviceService.getDevicesByHomeID(HOME_ID)).thenReturn(devices);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.listDevices(HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("devices"));
    }

    /**
     * 测试访客查看设备列表权限 - 应该成功（访客可以查看设备）
     */
    @Test
    void testGuestListDevices_Success() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟获取设备列表成功（只返回访客可访问的设备）
        List<Device> devices = Arrays.asList(
                createDevice(1L, "智能摄像头", HOME_ID, 6L) // 访客可访问的设备类型
        );
        when(deviceService.getDevicesByHomeID(HOME_ID)).thenReturn(devices);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.listDevices(HOME_ID);

        // 验证结果 - 访客应该能查看设备列表
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("devices"));
    }

    /**
     * 测试房主更新设备激活状态权限 - 应该成功
     */
    @Test
    void testHostUpdateDeviceActive_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟更新设备激活状态成功
        when(deviceService.updateLastActiveTime(DEVICE_ID, LocalDateTime.now())).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.updateActive(DEVICE_ID, HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("更新时间成功", response.getBody().get("message"));
    }

    /**
     * 测试访客更新设备激活状态权限 - 应该被拒绝
     */
    @Test
    void testGuestUpdateDeviceActive_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟更新失败（权限不足）
        when(deviceService.updateLastActiveTime(DEVICE_ID, LocalDateTime.now())).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = deviceManageController.updateActive(DEVICE_ID, HOME_ID);

        // 验证结果 - 访客应该被拒绝更新设备激活状态
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("设备不存在", response.getBody().get("message"));
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
