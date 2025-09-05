package com.example.manager.controller;

import com.example.manager.DTO.HomeAllSensorDataResponse;
import com.example.manager.service.HomeSensorDataService;
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

/**
 * 家庭传感器数据控制器
 * 提供家庭传感器数据查询相关的API接口
 */
@Tag(name = "家庭传感器数据接口", description = "家庭传感器数据的查询和管理接口")
@RestController
@RequestMapping("/home/{homeId}/sensor")
public class HomeSensorDataController {
    
    @Autowired
    private HomeSensorDataService homeSensorDataService;
    
    /**
     * 获取家庭所有传感器数据
     * @param homeId 家庭ID
     * @param headers HTTP请求头
     * @return 家庭传感器数据响应
     */
    @Operation(
        summary = "获取家庭所有传感器数据", 
        description = "获取指定家庭下所有传感器设备的最新数据和历史数据"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "家庭不存在或无传感器数据"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSensorData(
            @Parameter(description = "家庭ID", required = true) @PathVariable("homeId") Long homeId,
            @RequestHeader HttpHeaders headers) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            HomeAllSensorDataResponse sensorData = homeSensorDataService.getHomeAllSensorData(homeId);
            
            if (sensorData == null) {
                response.put("message", "家庭不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (sensorData.getSensorDevices() == null || sensorData.getSensorDevices().isEmpty()) {
                response.put("message", "该家庭暂无传感器设备");
                response.put("data", sensorData);
                return ResponseEntity.ok(response);
            }
            
            response.put("message", "获取传感器数据成功");
            response.put("data", sensorData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "获取传感器数据失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取指定设备的传感器历史数据
     * @param homeId 家庭ID
     * @param deviceId 设备ID
     * @param limit 限制条数（可选，默认50条）
     * @param headers HTTP请求头
     * @return 设备传感器历史数据
     */
    @Operation(
        summary = "获取设备传感器历史数据", 
        description = "获取指定设备的传感器历史数据记录"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "设备不存在或无数据"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/device/{deviceId}/history")
    public ResponseEntity<Map<String, Object>> getDeviceSensorHistory(
            @Parameter(description = "家庭ID", required = true) @PathVariable("homeId") Long homeId,
            @Parameter(description = "设备ID", required = true) @PathVariable("deviceId") Long deviceId,
            @Parameter(description = "限制条数", example = "50") @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
            @RequestHeader HttpHeaders headers) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<HomeAllSensorDataResponse.SensorData> historyData = 
                homeSensorDataService.getDeviceSensorHistory(deviceId, limit);
            
            if (historyData == null || historyData.isEmpty()) {
                response.put("message", "该设备暂无传感器数据");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("message", "获取设备历史数据成功");
            response.put("deviceId", deviceId);
            response.put("dataCount", historyData.size());
            response.put("data", historyData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "获取设备历史数据失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取家庭传感器数据概览
     * @param homeId 家庭ID
     * @param headers HTTP请求头
     * @return 传感器数据概览
     */
    @Operation(
        summary = "获取家庭传感器数据概览", 
        description = "获取家庭传感器设备的概览信息，包括设备数量、在线状态等"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "家庭不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getSensorOverview(
            @Parameter(description = "家庭ID", required = true) @PathVariable("homeId") Long homeId,
            @RequestHeader HttpHeaders headers) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            HomeAllSensorDataResponse sensorData = homeSensorDataService.getHomeAllSensorData(homeId);
            
            if (sensorData == null) {
                response.put("message", "家庭不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 统计概览信息
            int totalSensors = sensorData.getSensorDevices() != null ? sensorData.getSensorDevices().size() : 0;
            int onlineSensors = 0;
            int alertSensors = 0;
            
            if (sensorData.getSensorDevices() != null) {
                for (HomeAllSensorDataResponse.SensorDeviceData device : sensorData.getSensorDevices()) {
                    if (device.getOnlineStatus() != null && device.getOnlineStatus() == 1) {
                        onlineSensors++;
                    }
                    
                    // 检查是否有警报状态
                    if (device.getLatestData() != null && device.getLatestData().getStatusDescription() != null) {
                        String status = device.getLatestData().getStatusDescription();
                        if (status.contains("警报") || status.contains("过高") || status.contains("过低") || 
                            status.contains("检测到火焰") || status.contains("气体浓度过高")) {
                            alertSensors++;
                        }
                    }
                }
            }
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("homeId", homeId);
            overview.put("homeName", sensorData.getHomeName());
            overview.put("totalSensors", totalSensors);
            overview.put("onlineSensors", onlineSensors);
            overview.put("offlineSensors", totalSensors - onlineSensors);
            overview.put("alertSensors", alertSensors);
            overview.put("dataTime", sensorData.getDataTime());
            
            response.put("message", "获取传感器概览成功");
            response.put("overview", overview);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "获取传感器概览失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}