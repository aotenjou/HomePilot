package com.example.manager.controller;

import com.example.manager.entity.Device;
import com.example.manager.service.DevicePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 访客权限管理控制器
 * 专门处理访客用户的设备访问权限
 */
@Tag(name = "访客权限管理接口", description = "访客用户设备访问权限相关接口")
@RestController
@RequestMapping("/guest")
public class GuestPermissionController {
    
    @Autowired
    private DevicePermissionService devicePermissionService;
    
    @Operation(
            summary = "获取访客可访问的设备列表",
            description = "根据用户ID和家庭ID获取访客用户可访问的设备列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "403", description = "用户不是访客"),
            @ApiResponse(responseCode = "404", description = "没有可访问的设备")
    })
    @GetMapping("/{userId}/home/{homeId}/accessible-devices")
    public ResponseEntity<Map<String, Object>> getGuestAccessibleDevices(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "家庭ID") @PathVariable Long homeId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户是否为访客
        if (!devicePermissionService.isGuestUser(userId, homeId)) {
            response.put("message", "用户不是访客，请使用普通设备管理接口");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        List<Device> devices = devicePermissionService.getAccessibleDevices(userId, homeId);
        if (devices.isEmpty()) {
            response.put("message", "访客没有可访问的设备");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("devices", devices);
        response.put("userRole", "GUEST");
        response.put("accessibleDeviceTypes", devicePermissionService.getGuestAccessibleDeviceTypes());
        response.put("message", "访客可访问设备列表获取成功");
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "检查访客设备操作权限",
            description = "检查访客用户是否可以对指定设备执行指定操作"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "权限检查完成"),
            @ApiResponse(responseCode = "403", description = "用户不是访客或无权限")
    })
    @GetMapping("/{userId}/home/{homeId}/device/{deviceId}/operation/{operationId}/check")
    public ResponseEntity<Map<String, Object>> checkGuestDevicePermission(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "家庭ID") @PathVariable Long homeId,
            @Parameter(description = "设备ID") @PathVariable Long deviceId,
            @Parameter(description = "操作ID") @PathVariable Long operationId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户是否为访客
        if (!devicePermissionService.isGuestUser(userId, homeId)) {
            response.put("message", "用户不是访客");
            response.put("hasPermission", false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        boolean hasPermission = devicePermissionService.checkDevicePermission(userId, homeId, deviceId, operationId);
        response.put("hasPermission", hasPermission);
        response.put("message", hasPermission ? "访客有权限执行此操作" : "访客没有权限执行此操作");
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "获取访客权限说明",
            description = "获取访客用户的权限说明和限制"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/permission-info")
    public ResponseEntity<Map<String, Object>> getGuestPermissionInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("role", "GUEST");
        response.put("description", "访客用户");
        response.put("restrictions", List.of(
            "只能访问特定类型的设备（如传感器、监控摄像头等）",
            "只能执行查看类操作，不能进行控制操作",
            "不能添加、删除或修改设备",
            "不能访问敏感设备（如门锁、保险柜等）"
        ));
        response.put("accessibleDeviceTypes", devicePermissionService.getGuestAccessibleDeviceTypes());
        response.put("allowedOperations", List.of(1L, 2L)); // 假设1,2是查看类操作
        
        return ResponseEntity.ok(response);
    }
}
