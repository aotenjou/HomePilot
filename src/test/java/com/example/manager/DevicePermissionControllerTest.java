package test.java.com.example.manager;

import com.example.manager.controller.GuestPermissionController;
import com.example.manager.entity.Device;
import com.example.manager.service.DevicePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 设备权限控制器单元测试
 * 测试权限控制器的各种接口
 */
@ExtendWith(MockitoExtension.class)
public class DevicePermissionControllerTest {

    @Mock
    private DevicePermissionService devicePermissionService;

    @InjectMocks
    private GuestPermissionController guestPermissionController;

    private static final Long USER_ID = 1L;
    private static final Long HOME_ID = 1L;

    /**
     * 测试获取访客可访问设备列表 - 成功情况
     */
    @Test
    void testGetGuestAccessibleDevices_Success() {
        // 创建模拟设备列表
        List<Device> devices = Arrays.asList(
                createDevice(1L, 5L, "窗户1"),
                createDevice(2L, 6L, "摄像头1")
        );

        // 模拟访客用户
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(true);
        when(devicePermissionService.getAccessibleDevices(USER_ID, HOME_ID)).thenReturn(devices);
        when(devicePermissionService.getGuestAccessibleDeviceTypes()).thenReturn(Arrays.asList(5L, 6L));

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .getGuestAccessibleDevices(USER_ID, HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GUEST", response.getBody().get("userRole"));
        assertEquals(devices, response.getBody().get("devices"));
        assertEquals("访客可访问设备列表获取成功", response.getBody().get("message"));
    }

    /**
     * 测试获取访客可访问设备列表 - 用户不是访客
     */
    @Test
    void testGetGuestAccessibleDevices_UserNotGuest() {
        // 模拟非访客用户
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .getGuestAccessibleDevices(USER_ID, HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("用户不是访客，请使用普通设备管理接口", response.getBody().get("message"));
    }

    /**
     * 测试获取访客可访问设备列表 - 没有可访问设备
     */
    @Test
    void testGetGuestAccessibleDevices_NoDevices() {
        // 模拟访客用户但没有可访问设备
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(true);
        when(devicePermissionService.getAccessibleDevices(USER_ID, HOME_ID)).thenReturn(Collections.emptyList());

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .getGuestAccessibleDevices(USER_ID, HOME_ID);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("访客没有可访问的设备", response.getBody().get("message"));
    }

    /**
     * 测试检查访客设备操作权限 - 有权限
     */
    @Test
    void testCheckGuestDevicePermission_HasPermission() {
        // 模拟访客用户有权限
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(true);
        when(devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, 1L, 1L)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .checkGuestDevicePermission(USER_ID, HOME_ID, 1L, 1L);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("hasPermission"));
        assertEquals("访客有权限执行此操作", response.getBody().get("message"));
    }

    /**
     * 测试检查访客设备操作权限 - 无权限
     */
    @Test
    void testCheckGuestDevicePermission_NoPermission() {
        // 模拟访客用户无权限
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(true);
        when(devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, 1L, 3L)).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .checkGuestDevicePermission(USER_ID, HOME_ID, 1L, 3L);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("hasPermission"));
        assertEquals("访客没有权限执行此操作", response.getBody().get("message"));
    }

    /**
     * 测试检查访客设备操作权限 - 用户不是访客
     */
    @Test
    void testCheckGuestDevicePermission_UserNotGuest() {
        // 模拟非访客用户
        when(devicePermissionService.isGuestUser(USER_ID, HOME_ID)).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController
                .checkGuestDevicePermission(USER_ID, HOME_ID, 1L, 1L);

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("hasPermission"));
        assertEquals("用户不是访客", response.getBody().get("message"));
    }

    /**
     * 测试获取访客权限说明
     */
    @Test
    void testGetGuestPermissionInfo() {
        // 模拟访客可访问设备类型
        when(devicePermissionService.getGuestAccessibleDeviceTypes()).thenReturn(Arrays.asList(5L, 6L));

        // 执行测试
        ResponseEntity<Map<String, Object>> response = guestPermissionController.getGuestPermissionInfo();

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = response.getBody();
        assertEquals("GUEST", body.get("role"));
        assertEquals("访客用户", body.get("description"));
        assertNotNull(body.get("restrictions"));
        assertEquals(Arrays.asList(5L, 6L), body.get("accessibleDeviceTypes"));
        assertEquals(Arrays.asList(1L, 2L), body.get("allowedOperations"));
    }

    /**
     * 辅助方法：创建设备对象
     */
    private Device createDevice(Long id, Long typeId, String name) {
        Device device = new Device();
        device.setId(id);
        device.setTypeId(typeId);
        device.setDeviceName(name);
        return device;
    }
}
