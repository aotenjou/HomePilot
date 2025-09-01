package com.example.manager.controller;

import com.example.manager.DTO.DeviceOperationRequest;
import com.example.manager.DTO.MoveDeviceRequest;
import com.example.manager.entity.Mqttdata;
import com.example.manager.mqtt.Service.MqttConnectService;
import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.service.DeviceInteractService;
import com.example.manager.service.DevicePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "设备交互接口",description = "家庭内设备交互相关接口")
@RestController
@RequestMapping("/home/{homeId}/device")
public class DeviceInteractController {
    @Autowired
    private MqttSendmessageService mqttSendmessageService;

    @Autowired
    private DeviceInteractService deviceInteractService;

    @Autowired
    private MqttConnectService mqttConnectService;
    
    @Autowired
    private DevicePermissionService devicePermissionService;

    @Operation(
            summary = "发送设备操作命令",
            description = "根据设备ID和操作ID发送设备操作，若设备不在线将通过mqtt推送命令"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description="命令已发送"),
            @ApiResponse(responseCode = "404",description="设备未在线")
    })
    @PostMapping("/{deviceId}/operation/{operationId}")
    public ResponseEntity<Map<String, Object>> sendCommand(@Parameter(description = "当前用户ID（从请求属性获取）") @RequestAttribute("currentUserId") Long userId,
                                                           @PathVariable("deviceId") Long deviceId,
                                                           @PathVariable("operationId") Long operationId,
                                                           @PathVariable("homeId") Long homeId,
                                                           @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        // 检查用户权限
        if (!devicePermissionService.checkDevicePermission(userId, homeId, deviceId, operationId)) {
            response.put("message", "没有权限进行此操作");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if(!deviceInteractService.checkDeviceOnlineStatus(deviceId)) {
            response.put("message", "设备未在线");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        deviceInteractService.sendCommand(deviceId.toString(), operationId.toString());
        response.put("message", "命令已发送");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "移动设备到新房间",
            description = "根据设备 ID 和目标房间 ID 移动设备位置。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "设备移动成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误或运行时错误"),
            @ApiResponse(responseCode = "500", description = "设备移动失败或服务器错误")
    })
    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> moveDevice(@RequestBody MoveDeviceRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = deviceInteractService.moveDevice(request.getDeviceId(), request.getRoomId());
            if (success) {
                response.put("message", "设备移动成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "设备移动失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("message", "参数错误：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("message", "服务器错误：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @Operation(summary = "连接设备并接收信息", description = "连接设备并调用接口接收信息")
    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connectDevice() {
        Map<String, Object> response = new HashMap<>();
        mqttConnectService.connect();
        response.put("message", "设备连接成功");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "断开设备连接", description = "断开设备连接")
    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, Object>> disconnectDevice() {
        Map<String, Object> response = new HashMap<>();
        mqttConnectService.disConnect();
        response.put("message", "设备断开连接成功");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取数据信息", description = "通过对应的设备id获取设备的数据信息")
    @GetMapping("{deviceId}/getData")
    public ResponseEntity<Map<String, Object>> getData(@PathVariable("deviceId") Long deviceId) {
        Map<String, Object> response = new HashMap<>();
        List<Mqttdata> data = deviceInteractService.getDeviceData(deviceId);
        if(data.isEmpty()) {
            response.put("message", "没有任何信息");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
