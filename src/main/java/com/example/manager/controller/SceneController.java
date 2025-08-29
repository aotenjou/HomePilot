package com.example.manager.controller;

import com.example.manager.DTO.AddSceneRequest;
import com.example.manager.DTO.SceneDeviceView;
import com.example.manager.DTO.StartSceneRequest;
import com.example.manager.entity.*;
import com.example.manager.mqtt.Service.MqttConnectService;
import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.service.DeviceInteractService;
import com.example.manager.service.HomeService;
import com.example.manager.service.SceneService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name="场景接口",description="家庭自定义场景的相关接口")
@RestController
@RequestMapping("/home/{homeId}/scene")
public class SceneController {
    @Autowired
    private SceneService sceneService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private MqttConnectService mqttConnectService;

    @Autowired
    private DeviceInteractService deviceInteractService;

    @Operation(
            summary = "获取某家庭下可视设备列表（用于添加场景）",
            description = "传入家庭 ID，获取该家庭下可用于配置场景的设备信息"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "设备获取成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在或设备未发现")
    })
    @GetMapping("/view/{sceneId}/device")
    public ResponseEntity<Map<String, Object>> deviceView(@PathVariable("homeId") Long homeId,
                                                          @PathVariable("sceneId") Long sceneId,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!sceneService.checkScene(sceneId)) {
            response.put("message", "不存在此场景");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<SceneDeviceView> devices = sceneService.getSceneDeviceView(sceneId);
        if(devices == null) {
            response.put("message", "暂无设备");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("devices", devices);
        response.put("message", "查看成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "创建新场景",
            description = "为指定家庭创建一个自动化场景，可配置多个设备及其状态，传入场景名称、描述、状态、结束时间等信息"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "场景创建成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在"),
            @ApiResponse(responseCode = "500", description = "创建失败")
    })
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addScene(@RequestBody AddSceneRequest request,
                                                        @PathVariable("homeId") Long homeId,
                                                        @RequestAttribute("currentUserId") Long userId,
                                                        @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if(request.getDeviceOperation().isEmpty()) {
            sceneService.createSceneWithoutDevice(userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime());
        } else {
            sceneService.createScene(userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime(), request.getDeviceOperation());
        }

        response.put("message", "创建场景成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "删除场景", description = "通过sceneId删除场景")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在"),
            @ApiResponse(responseCode = "500", description = "删除场景失败")
    })
    @DeleteMapping("/delete/{sceneId}")
    public ResponseEntity<Map<String, Object>> deleteScene(@PathVariable Long sceneId,
                                                           @PathVariable Long homeId,
                                                           @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!sceneService.checkScene(sceneId)) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        sceneService.deleteScene(sceneId);
        if(sceneService.checkScene(sceneId)) {
            response.put("message", "删除场景失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "删除成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "获取场景列表", description = "通过路径参数homeId获取指定家庭下的场景列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取场景列表成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在或者没找到场景"),
            @ApiResponse(responseCode = "500", description = "获取场景列表失败")
    })
    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewScene(@PathVariable("homeId") Long homeId,
                                                         @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<Scene> scenes = sceneService.getHomeScene(homeId);
        if(scenes.isEmpty()) {
            response.put("message", "没有此家庭下的场景");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("scenes", scenes);
        response.put("message", "获取成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "启动场景", description = "通过场景ID启动场景")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "启动成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在"),
            @ApiResponse(responseCode = "500", description = "启动失败")
    })
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startScene(@RequestBody StartSceneRequest request,
                                                          @RequestAttribute("currentUserId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        if (!sceneService.checkScene(request.getSceneId())) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<DeviceInScene> deviceOperation = sceneService.getDeviceOperation(request.getSceneId());

        mqttConnectService.connect();
        Map<String, Object> operationExecutionStatus = new HashMap<>();
        for(DeviceInScene deviceInScene : deviceOperation) {
            if(!deviceInteractService.checkDeviceOnlineStatus(deviceInScene.getDeviceId())) {
                operationExecutionStatus.put(deviceInScene.getDeviceId().toString(), "未在线");
                continue;
            }
            deviceInteractService.sendCommand(deviceInScene.getDeviceId().toString(), deviceInScene.getDeviceOperationId().toString());
            operationExecutionStatus.put(deviceInScene.getDeviceId().toString(), "已执行");
        }
        mqttConnectService.disConnect();
        response.put("message", "场景已开启");
        response.put("设备开启情况", operationExecutionStatus);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "停止场景", description = "通过场景ID停止场景")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "停止成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在"),
            @ApiResponse(responseCode = "500", description = "停止失败")
    })
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopScene(@RequestBody StartSceneRequest request,
                                                         @RequestAttribute("currentUserId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        if(!sceneService.checkScene(request.getSceneId())) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<DeviceInScene> deviceOperation = sceneService.getDeviceOperation(request.getSceneId());

        mqttConnectService.connect();
        for(DeviceInScene deviceInScene : deviceOperation) {
            if(!deviceInteractService.checkDeviceOnlineStatus(deviceInScene.getDeviceId())) {
                continue;
            }
            deviceInteractService.sendCommand(deviceInScene.getDeviceId().toString(), deviceInScene.getDeviceOperationId().toString());
        }
        mqttConnectService.disConnect();
        response.put("message", "场景已关闭");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "修改场景参数和设备表", description = "通过场景ID以及传入的参数表修改场景的参数和设备表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在"),
            @ApiResponse(responseCode = "500", description = "修改失败")
    })
    @PostMapping("/update/{sceneId}")
    public ResponseEntity<Map<String, Object>> updateScene(@RequestBody AddSceneRequest request,
                                                          @PathVariable("sceneId") Long sceneId,
                                                          @PathVariable("homeId") Long homeId,
                                                          @RequestAttribute("currentUserId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        if(!sceneService.checkScene(sceneId)) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(request.getDeviceOperation().isEmpty()) {
            sceneService.updateSceneWithoutDevice(sceneId, userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime());
        } else {
            sceneService.updateScene(sceneId, userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime(), request.getDeviceOperation());
        }

        response.put("message", "更新场景成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
