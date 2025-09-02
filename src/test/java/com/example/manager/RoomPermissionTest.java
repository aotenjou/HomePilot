package com.example.manager;

import com.example.manager.DTO.CreateRoomRequest;
import com.example.manager.controller.RoomController;
import com.example.manager.entity.Room;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.HomeService;
import com.example.manager.service.RoomService;
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
 * 房间管理权限测试
 * 测试不同角色在房间管理方面的权限差异
 */
@ExtendWith(MockitoExtension.class)
public class RoomPermissionTest {

    @Mock
    private RoomService roomService;

    @Mock
    private HomeService homeService;

    @Mock
    private UserHomeMapper userHomeMapper;

    @InjectMocks
    private RoomController roomController;

    private static final Long USER_ID = 1L;
    private static final Long HOME_ID = 1L;
    private static final Long ROOM_ID = 1L;

    /**
     * 测试房主创建房间权限 - 应该成功
     */
    @Test
    void testHostCreateRoom_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟房间创建成功
        when(roomService.createRoom(HOME_ID, "客厅")).thenReturn(ROOM_ID);
        when(roomService.checkRoom(ROOM_ID, HOME_ID)).thenReturn(true);

        // 创建请求
        CreateRoomRequest request = new CreateRoomRequest(HOME_ID, "客厅", "家庭客厅");

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.createRoom(request, null);

        // 验证结果
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("添加房间成功", response.getBody().get("message"));
    }

    /**
     * 测试家庭成员创建房间权限 - 应该成功（假设默认权限允许）
     */
    @Test
    void testMemberCreateRoom_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟房间创建成功
        when(roomService.createRoom(HOME_ID, "厨房")).thenReturn(ROOM_ID);
        when(roomService.checkRoom(ROOM_ID, HOME_ID)).thenReturn(true);

        // 创建请求
        CreateRoomRequest request = new CreateRoomRequest(HOME_ID, "厨房", "家庭厨房");

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.createRoom(request, null);

        // 验证结果
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("添加房间成功", response.getBody().get("message"));
    }

    /**
     * 测试访客创建房间权限 - 应该被拒绝
     */
    @Test
    void testGuestCreateRoom_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟房间创建失败（权限不足）
        when(roomService.createRoom(HOME_ID, "书房")).thenReturn(null);
        when(roomService.checkRoom(null, HOME_ID)).thenReturn(false);

        // 创建请求
        CreateRoomRequest request = new CreateRoomRequest(HOME_ID, "书房", "访客书房");

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.createRoom(request, null);

        // 验证结果 - 访客应该被拒绝创建房间
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("添加房间失败", response.getBody().get("message"));
    }

    /**
     * 测试房主删除房间权限 - 应该成功
     */
    @Test
    void testHostDeleteRoom_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟房间存在且删除成功
        when(roomService.checkRoom(ROOM_ID, HOME_ID)).thenReturn(true);
        // 注意：这里我们不mock deleteRoom，因为它返回void

        // 创建房间对象
        Room room = new Room();
        room.setId(ROOM_ID);
        room.setHomeId(HOME_ID);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.deleteRoom(room, HOME_ID, null);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("删除成功", response.getBody().get("message"));
    }

    /**
     * 测试家庭成员删除房间权限 - 应该成功（假设默认权限允许）
     */
    @Test
    void testMemberDeleteRoom_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟房间存在且删除成功
        when(roomService.checkRoom(ROOM_ID, HOME_ID)).thenReturn(true);

        // 创建房间对象
        Room room = new Room();
        room.setId(ROOM_ID);
        room.setHomeId(HOME_ID);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.deleteRoom(room, HOME_ID, null);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("删除成功", response.getBody().get("message"));
    }

    /**
     * 测试访客删除房间权限 - 应该被拒绝
     */
    @Test
    void testGuestDeleteRoom_Denied() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟房间不存在（权限不足）
        when(roomService.checkRoom(ROOM_ID, HOME_ID)).thenReturn(false);

        // 创建房间对象
        Room room = new Room();
        room.setId(ROOM_ID);
        room.setHomeId(HOME_ID);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.deleteRoom(room, HOME_ID, null);

        // 验证结果 - 访客应该被拒绝删除房间
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("家庭不存在此房间", response.getBody().get("message"));
    }

    /**
     * 测试房主查看房间列表权限 - 应该成功
     */
    @Test
    void testHostListRooms_Success() {
        // 模拟房主角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.HOST.getCode());

        // 模拟家庭存在且有房间
        when(homeService.checkHome(HOME_ID)).thenReturn(true);
        List<Room> rooms = Arrays.asList(
                createRoom(1L, HOME_ID, "客厅"),
                createRoom(2L, HOME_ID, "厨房")
        );
        when(roomService.getRoom(HOME_ID)).thenReturn(rooms);
        when(roomService.checkRoom(1L, HOME_ID)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.listRooms(HOME_ID, null);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("查看成功", response.getBody().get("message"));
        assertNotNull(response.getBody().get("Rooms"));
    }

    /**
     * 测试家庭成员查看房间列表权限 - 应该成功
     */
    @Test
    void testMemberListRooms_Success() {
        // 模拟家庭成员角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.MEMBER.getCode());

        // 模拟家庭存在且有房间
        when(homeService.checkHome(HOME_ID)).thenReturn(true);
        List<Room> rooms = Arrays.asList(
                createRoom(1L, HOME_ID, "客厅"),
                createRoom(2L, HOME_ID, "厨房")
        );
        when(roomService.getRoom(HOME_ID)).thenReturn(rooms);
        when(roomService.checkRoom(1L, HOME_ID)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.listRooms(HOME_ID, null);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("查看成功", response.getBody().get("message"));
    }

    /**
     * 测试访客查看房间列表权限 - 应该成功（访客可以查看）
     */
    @Test
    void testGuestListRooms_Success() {
        // 模拟访客角色
        when(userHomeMapper.selectRoleByUserIdAndHomeId(USER_ID, HOME_ID))
                .thenReturn(UserHome.Role.GUEST.getCode());

        // 模拟家庭存在且有房间
        when(homeService.checkHome(HOME_ID)).thenReturn(true);
        List<Room> rooms = Arrays.asList(
                createRoom(1L, HOME_ID, "客厅")
        );
        when(roomService.getRoom(HOME_ID)).thenReturn(rooms);
        when(roomService.checkRoom(1L, HOME_ID)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = roomController.listRooms(HOME_ID, null);

        // 验证结果 - 访客应该能查看房间列表
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("查看成功", response.getBody().get("message"));
    }

    /**
     * 辅助方法：创建房间对象
     */
    private Room createRoom(Long id, Long homeId, String name) {
        Room room = new Room();
        room.setId(id);
        room.setHomeId(homeId);
        room.setName(name);
        return room;
    }
}
