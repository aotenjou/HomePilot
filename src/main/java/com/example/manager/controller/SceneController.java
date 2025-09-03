package com.example.manager.controller;

import com.example.manager.DTO.AddSceneRequest;
import com.example.manager.DTO.OperationOfDevice;
import com.example.manager.service.HomeService;
import com.example.manager.DTO.SceneDeviceView;
import com.example.manager.DTO.StartSceneRequest;
import com.example.manager.service.DeviceInteractService;
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
    private DeviceInteractService deviceInteractService;

    @Operation(
            summary = "创建新场景",
            description = "为指定家庭创建一个自动化场景，可配置多个设备及其状态"
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

        if (request.getName() == null || request.getName().isBlank()) {
            response.put("message", "场景名称不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (request.getDescription() == null) {
            request.setDescription("");
        }

        if (!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            if (request.getDeviceOperation() == null || request.getDeviceOperation().isEmpty()) {
                sceneService.createSceneWithoutDevice(userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime());
            } else {
                List<OperationOfDevice> filtered = request.getDeviceOperation().stream()
                        .filter(op -> op != null && op.getDeviceId() != null && op.getOperationId() != null)
                        .toList();
                if (filtered.isEmpty()) {
                    sceneService.createSceneWithoutDevice(userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime());
                } else {
                    sceneService.createScene(userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime(), filtered);
                }
            }
            response.put("message", "创建场景成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "创建场景失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(
            summary = "获取场景列表",
            description = "根据家庭ID获取该家庭下的场景列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewScene(@PathVariable("homeId") Long homeId,
                                                         @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        if (!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            response.put("scenes", List.of());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        List<com.example.manager.entity.Scene> scenes = sceneService.getHomeScene(homeId);
        response.put("scenes", scenes == null ? List.of() : scenes);
        response.put("message", "获取成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "获取场景设备视图",
            description = "根据场景ID获取该场景下的设备视图列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/view/{sceneId}/device")
    public ResponseEntity<Map<String, Object>> deviceView(@PathVariable("homeId") Long homeId,
                                                          @PathVariable("sceneId") Long sceneId,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        if (!sceneService.checkScene(sceneId)) {
            response.put("message", "不存在此场景");
            response.put("devices", List.of());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        List<SceneDeviceView> devices = sceneService.getSceneDeviceView(sceneId);
        response.put("devices", devices == null ? List.of() : devices);
        response.put("message", "获取成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "启动场景",
            description = "根据场景ID启动场景中的设备操作"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "启动成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在")
    })
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startScene(@RequestBody StartSceneRequest request,
                                                          @RequestAttribute("currentUserId") Long userId,
                                                          @PathVariable("homeId") Long homeId) {
        Map<String, Object> response = new HashMap<>();
        if (!sceneService.checkScene(request.getSceneId())) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<com.example.manager.entity.DeviceInScene> deviceOperation = sceneService.getDeviceOperation(request.getSceneId());
        for (com.example.manager.entity.DeviceInScene item : deviceOperation) {
            if (!deviceInteractService.checkDeviceOnlineStatus(item.getDeviceId())) {
                continue;
            }
            deviceInteractService.sendCommand(item.getDeviceId().toString(), item.getDeviceOperationId().toString());
        }
        response.put("message", "启动成功");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "停止场景",
            description = "根据场景ID停止场景中的设备操作"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "停止成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在")
    })
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopScene(@RequestBody StartSceneRequest request,
                                                         @RequestAttribute("currentUserId") Long userId,
                                                         @PathVariable("homeId") Long homeId) {
        Map<String, Object> response = new HashMap<>();
        if (!sceneService.checkScene(request.getSceneId())) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<com.example.manager.entity.DeviceInScene> deviceOperation = sceneService.getDeviceOperation(request.getSceneId());
        for (com.example.manager.entity.DeviceInScene item : deviceOperation) {
            if (!deviceInteractService.checkDeviceOnlineStatus(item.getDeviceId())) {
                continue;
            }
            // 停止逻辑按现有命令通道发送对应 operationId
            deviceInteractService.sendCommand(item.getDeviceId().toString(), item.getDeviceOperationId().toString());
        }
        response.put("message", "停止成功");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "更新场景",
            description = "根据场景ID与请求体更新场景信息与设备操作"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在")
    })
    @PostMapping("/update/{sceneId}")
    public ResponseEntity<Map<String, Object>> updateScene(@RequestBody AddSceneRequest request,
                                                           @PathVariable("sceneId") Long sceneId,
                                                           @PathVariable("homeId") Long homeId,
                                                           @RequestAttribute("currentUserId") Long userId) {
        Map<String, Object> response = new HashMap<>();

        if (!sceneService.checkScene(sceneId)) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            response.put("message", "场景名称不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (request.getDescription() == null) {
            request.setDescription("");
        }

        try {
            if (request.getDeviceOperation() == null || request.getDeviceOperation().isEmpty()) {
                sceneService.updateSceneWithoutDevice(sceneId, userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime());
            } else {
                List<OperationOfDevice> filtered = request.getDeviceOperation().stream()
                        .filter(op -> op != null && op.getDeviceId() != null && op.getOperationId() != null)
                        .toList();
                sceneService.updateScene(sceneId, userId, homeId, request.getName(), request.getDescription(), request.getStatus(), request.getStartTime(), request.getEndTime(), filtered);
            }
            response.put("message", "更新成功");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(
            summary = "删除场景",
            description = "根据场景ID删除场景"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "场景不存在")
    })
    @DeleteMapping("/delete/{sceneId}")
    public ResponseEntity<Map<String, Object>> deleteScene(@PathVariable("homeId") Long homeId,
                                                           @PathVariable("sceneId") Long sceneId,
                                                           @RequestAttribute("currentUserId") Long userId,
                                                           @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        if (!sceneService.checkScene(sceneId)) {
            response.put("message", "场景不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        sceneService.deleteScene(sceneId);
        response.put("message", "删除成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}