package com.example.manager.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceType;
import com.example.manager.service.DeviceManageService;
import com.example.manager.service.DevicePermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="设备管理接口",description = "设备增删改查、获取设备类型列表接口")
@RestController
@RequestMapping("/home/{homeId}/room/device")
public class DeviceManageController {

    @Autowired
    private DeviceManageService deviceService;

    @Autowired
    private DevicePermissionService devicePermissionService;

    @Operation(
            summary = "获取设备类型列表",
            description = "返回系统中定义的所有设备类型列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description="设备类型列表获取成功")
    })
    @GetMapping("/type/list")
    public ResponseEntity<List<DeviceType>> listDeviceTypes() {
        List<DeviceType> typeList = deviceService.getAllDeviceTypes();
        return ResponseEntity.ok(typeList);
    }

    @Operation(summary = "添加设备",description = "添加设备到指定房间，设备信息通过请求体传入。")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description="设备添加成功"),
            @ApiResponse(responseCode = "500",description="设备添加失败")
    })
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDevice(@RequestBody Device device) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long deviceID = deviceService.addDevice(device);
            response.put("status", "success");
            response.put("message", "添加设备成功");
            response.put("deviceId", deviceID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "添加设备失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "列出当前家庭下的设备", description = "根据家庭ID获取该家庭下的所有设备。")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listDevices(@Parameter(description = "家庭ID", example = "1")@PathVariable("homeId") Long homeId) {
        Map<String, Object> response = new HashMap<>();
        List<Device> devices = deviceService.getDevicesByHomeID(homeId);
        response.put("devices", devices);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "更新设备信息", description = "修改设备的基础信息，如名称、房间等。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "更新失败")
    })
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateDevice(@RequestBody Device device) {
        Map<String, Object> response = new HashMap<>();
        boolean success = deviceService.updateDevice(device);
        if (success) {
            response.put("status", "success");
            response.put("message", "设备信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "更新失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "删除设备", description = "根据设备ID删除设备。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "删除失败")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteDevice(@Parameter(description = "设备ID", example = "1")@RequestParam Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = deviceService.removeDevice(id);
        if (success) {
            response.put("status", "success");
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "删除失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "更新设备激活状态", description = "更新指定设备的最后活跃时间为当前时间。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新时间成功"),
            @ApiResponse(responseCode = "400", description = "设备不存在")
    })
    @PostMapping("/active")
    public ResponseEntity<Map<String, Object>> updateActive(@RequestParam Long id, @PathVariable Long homeId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = deviceService.updateLastActiveTime(id, LocalDateTime.now());
        if (success) {
            response.put("status", "success");
            response.put("message", "更新时间成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "设备不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "获取用户可访问设备", description = "根据用户ID和家庭ID获取该用户在家庭中可访问的所有设备。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "没有可访问的设备")
    })
    @GetMapping("/accessibleDevices")
    public ResponseEntity<Map<String, Object>> getAccessibleDevices(@Parameter(hidden = true)@RequestAttribute("currentUserId") Long userId,
                                                                    @PathVariable("homeId") Long homeId) {
        Map<String, Object> response = new HashMap<>();
        List<Device> devices = devicePermissionService.getAccessibleDevices(userId, homeId);
        if (devices.isEmpty()) {
            response.put("message", "没有可访问的设备");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("devices", devices);
        response.put("userRole", devicePermissionService.getUserRole(userId, homeId));
        response.put("isGuest", devicePermissionService.isGuestUser(userId, homeId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

