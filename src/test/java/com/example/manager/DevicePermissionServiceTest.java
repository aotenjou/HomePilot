package com.example.manager;

import com.example.manager.entity.Device;
import com.example.manager.entity.UserCustomPermission;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.RoleDefaultPermissionMapper;
import com.example.manager.mapper.UserCustomPermissionMapper;
import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.DevicePermissionService;
import com.example.manager.service.serviceImpl.DevicePermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 设备权限服务单元测试
 * 测试不同角色对于设备的访问权限
 */
@ExtendWith(MockitoExtension.class)
public class DevicePermissionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DevicePermissionServiceTest.class);

    @Mock
    private UserCustomPermissionMapper userCustomPermissionMapper;

    @Mock
    private RoleDefaultPermissionMapper roleDefaultPermissionMapper;

    @Mock
    private UserHomeMapper userHomeMapper;

    @Mock
    private DeviceMapper deviceMapper;

    @InjectMocks
    private DevicePermissionServiceImpl devicePermissionService;

    // 测试数据
    private static final Long USER_ID = 1L;
    private static final Long HOME_ID = 1L;
    private static final Long DEVICE_ID = 1L;
    private static final Long OPERATION_ID = 1L;

    // 访客可访问的设备类型
    private static final List<Long> GUEST_ACCESSIBLE_TYPES = Arrays.asList(5L, 6L);

    @BeforeEach
    void setUp() {
        logger.info("开始设置测试环境");
    }

    /**
     * 测试房主权限 - 房主应该能访问所有设备
     */
    @Test
    void testHostPermission_ShouldAccessAllDevices() {
        logger.info("测试房主权限 - 房主应该能访问所有设备");

        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟没有自定义权限，返回角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.HOST.getCode(), DEVICE_ID, OPERATION_ID))
                .thenReturn(true);

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果
        assertTrue(hasPermission, "房主应该有权限访问所有设备");
        logger.info("房主权限测试通过");
    }

    /**
     * 测试家庭成员权限 - 基于角色默认权限
     */
    @Test
    void testMemberPermission_BasedOnDefaultRole() {
        logger.info("测试家庭成员权限 - 基于角色默认权限");

        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟没有自定义权限，返回角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.MEMBER.getCode(), DEVICE_ID, OPERATION_ID))
                .thenReturn(true);

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果
        assertTrue(hasPermission, "家庭成员应该有权限执行允许的操作");
        logger.info("家庭成员权限测试通过");
    }

    /**
     * 测试家庭成员无权限情况
     */
    @Test
    void testMemberPermission_NoPermission() {
        logger.info("测试家庭成员无权限情况");

        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟没有自定义权限，角色默认权限为false
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.MEMBER.getCode(), DEVICE_ID, OPERATION_ID))
                .thenReturn(false);

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果
        assertFalse(hasPermission, "家庭成员不应该有权限执行被禁止的操作");
        logger.info("家庭成员无权限测试通过");
    }

    /**
     * 测试访客权限 - 只能访问特定设备类型
     */
    @Test
    void testGuestPermission_AccessibleDeviceType() {
        logger.info("测试访客权限 - 访问允许的设备类型");

        // 创建访客可访问的设备
        Device accessibleDevice = new Device();
        accessibleDevice.setId(DEVICE_ID);
        accessibleDevice.setTypeId(5L); // 窗户 - 访客可访问

        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟没有自定义权限，角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.GUEST.getCode(), DEVICE_ID, OPERATION_ID))
                .thenReturn(true);

        // 模拟设备查询
        when(deviceMapper.selectById(DEVICE_ID)).thenReturn(Arrays.asList(accessibleDevice));

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果
        assertTrue(hasPermission, "访客应该能访问允许的设备类型");
        logger.info("访客可访问设备类型测试通过");
    }

    /**
     * 测试访客权限 - 不能访问禁止的设备类型
     */
    @Test
    void testGuestPermission_InaccessibleDeviceType() {
        logger.info("测试访客权限 - 访问禁止的设备类型");

        // 创建访客不可访问的设备
        Device inaccessibleDevice = new Device();
        inaccessibleDevice.setId(DEVICE_ID);
        inaccessibleDevice.setTypeId(1L); // 门锁 - 访客不可访问

        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟没有自定义权限，角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.GUEST.getCode(), DEVICE_ID, OPERATION_ID))
                .thenReturn(true);

        // 模拟设备查询
        when(deviceMapper.selectById(DEVICE_ID)).thenReturn(Arrays.asList(inaccessibleDevice));

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果
        assertFalse(hasPermission, "访客不应该能访问禁止的设备类型");
        logger.info("访客不可访问设备类型测试通过");
    }

    /**
     * 测试访客权限 - 只能执行查看操作
     */
    @Test
    void testGuestPermission_ViewOnlyOperations() {
        logger.info("测试访客权限 - 只能执行查看操作");

        // 创建访客可访问的设备
        Device accessibleDevice = new Device();
        accessibleDevice.setId(DEVICE_ID);
        accessibleDevice.setTypeId(5L); // 窗户 - 访客可访问

        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟没有自定义权限，角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, 1L))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.GUEST.getCode(), DEVICE_ID, 1L))
                .thenReturn(true);

        // 模拟设备查询
        when(deviceMapper.selectById(DEVICE_ID)).thenReturn(Arrays.asList(accessibleDevice));

        // 执行测试 - 查看操作（操作ID=1）
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, 1L);

        // 验证结果
        assertTrue(hasPermission, "访客应该能执行查看操作（操作ID=1）");
        logger.info("访客查看操作权限测试通过");
    }

    /**
     * 测试访客权限 - 不能执行控制操作
     */
    @Test
    void testGuestPermission_NoControlOperations() {
        logger.info("测试访客权限 - 不能执行控制操作");

        // 创建访客可访问的设备
        Device accessibleDevice = new Device();
        accessibleDevice.setId(DEVICE_ID);
        accessibleDevice.setTypeId(5L); // 窗户 - 访客可访问

        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟没有自定义权限，角色默认权限为true
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, 3L))
                .thenReturn(null);
        when(roleDefaultPermissionMapper.selectPermission(UserHome.Role.GUEST.getCode(), DEVICE_ID, 3L))
                .thenReturn(true);

        // 模拟设备查询
        when(deviceMapper.selectById(DEVICE_ID)).thenReturn(Arrays.asList(accessibleDevice));

        // 执行测试 - 控制操作（操作ID=3）
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, 3L);

        // 验证结果
        assertFalse(hasPermission, "访客不应该能执行控制操作（操作ID=3）");
        logger.info("访客控制操作权限测试通过");
    }

    /**
     * 测试用户自定义权限覆盖默认权限
     */
    @Test
    void testCustomPermission_OverridesDefault() {
        logger.info("测试用户自定义权限覆盖默认权限");

        // 创建自定义权限 - 明确禁止访问
        UserCustomPermission customPermission = new UserCustomPermission();
        customPermission.setUserId(USER_ID);
        customPermission.setDeviceId(DEVICE_ID);
        customPermission.setOperationId(OPERATION_ID);
        customPermission.setHasPermission(false);

        // 模拟有自定义权限（禁止访问）
        when(userCustomPermissionMapper.selectPermission(USER_ID, DEVICE_ID, OPERATION_ID))
                .thenReturn(customPermission);

        // 执行测试
        boolean hasPermission = devicePermissionService.checkDevicePermission(USER_ID, HOME_ID, DEVICE_ID, OPERATION_ID);

        // 验证结果 - 自定义权限应该覆盖默认权限
        assertFalse(hasPermission, "用户自定义权限应该覆盖角色默认权限");
        logger.info("自定义权限覆盖测试通过");
    }

    /**
     * 测试获取用户可访问设备列表 - 房主
     */
    @Test
    void testGetAccessibleDevices_Host() {
        logger.info("测试获取用户可访问设备列表 - 房主");

        // 创建设备列表
        List<Device> allDevices = Arrays.asList(
                createDevice(1L, 1L, "门锁"),
                createDevice(2L, 5L, "窗户"),
                createDevice(3L, 6L, "摄像头")
        );

        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());
        when(deviceMapper.selectDevicesByHomeId(HOME_ID)).thenReturn(allDevices);

        // 执行测试
        List<Device> accessibleDevices = devicePermissionService.getAccessibleDevices(USER_ID, HOME_ID);

        // 验证结果 - 房主应该能访问所有设备
        assertEquals(3, accessibleDevices.size(), "房主应该能访问所有设备");
        logger.info("房主可访问设备列表测试通过");
    }

    /**
     * 测试获取用户可访问设备列表 - 访客
     */
    @Test
    void testGetAccessibleDevices_Guest() {
        logger.info("测试获取用户可访问设备列表 - 访客");

        // 创建设备列表（包含访客可访问和不可访问的设备）
        List<Device> allDevices = Arrays.asList(
                createDevice(1L, 1L, "门锁"),      // 不可访问
                createDevice(2L, 5L, "窗户"),      // 可访问
                createDevice(3L, 6L, "摄像头"),    // 可访问
                createDevice(4L, 2L, "灯泡")       // 不可访问
        );

        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());
        when(deviceMapper.selectDevicesByHomeId(HOME_ID)).thenReturn(allDevices);

        // 执行测试
        List<Device> accessibleDevices = devicePermissionService.getAccessibleDevices(USER_ID, HOME_ID);

        // 验证结果 - 访客只能访问窗户和摄像头
        assertEquals(2, accessibleDevices.size(), "访客只能访问特定类型的设备");
        assertTrue(accessibleDevices.stream().anyMatch(d -> d.getTypeId().equals(5L)), "访客应该能访问窗户");
        assertTrue(accessibleDevices.stream().anyMatch(d -> d.getTypeId().equals(6L)), "访客应该能访问摄像头");
        logger.info("访客可访问设备列表测试通过");
    }

    /**
     * 测试获取访客可访问设备类型
     */
    @Test
    void testGetGuestAccessibleDeviceTypes() {
        logger.info("测试获取访客可访问设备类型");

        // 执行测试
        List<Long> accessibleTypes = devicePermissionService.getGuestAccessibleDeviceTypes();

        // 验证结果
        assertEquals(GUEST_ACCESSIBLE_TYPES, accessibleTypes, "访客可访问设备类型应该正确返回");
        assertTrue(accessibleTypes.contains(5L), "应该包含窗户类型");
        assertTrue(accessibleTypes.contains(6L), "应该包含摄像头类型");
        logger.info("访客可访问设备类型测试通过");
    }

    /**
     * 测试判断用户是否为访客 - 访客用户
     */
    @Test
    void testIsGuestUser_Guest() {
        logger.info("测试判断用户是否为访客 - 访客用户");

        // 测试访客用户
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        assertTrue(devicePermissionService.isGuestUser(USER_ID, HOME_ID), "应该识别出访客用户");
        logger.info("访客用户身份判断测试通过");
    }

    /**
     * 测试判断用户是否为访客 - 非访客用户（房主）
     */
    @Test
    void testIsGuestUser_Host() {
        logger.info("测试判断用户是否为访客 - 房主");

        // 测试非访客用户（房主）
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        assertFalse(devicePermissionService.isGuestUser(USER_ID, HOME_ID), "不应该将房主识别为访客");
        logger.info("房主身份判断测试通过");
    }

    /**
     * 测试判断用户是否为访客 - 非访客用户（家庭成员）
     */
    @Test
    void testIsGuestUser_Member() {
        logger.info("测试判断用户是否为访客 - 家庭成员");

        // 测试非访客用户（家庭成员）
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        assertFalse(devicePermissionService.isGuestUser(USER_ID, HOME_ID), "不应该将家庭成员识别为访客");
        logger.info("家庭成员身份判断测试通过");
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
