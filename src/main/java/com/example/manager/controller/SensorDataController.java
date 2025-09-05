package com.example.manager.controller;

import com.example.manager.entity.Device;
import com.example.manager.entity.Mqttdata;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.MqttdataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorDataController {

    @Autowired
    private MqttdataMapper mqttdataMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 获取设备的最新传感器数据
     */
    @GetMapping("/device/{deviceId}/latest")
    public ResponseEntity<Map<String, Object>> getLatestSensorData(
            @PathVariable Long deviceId) {
        
        try {
            // 获取设备信息
            List<Device> devices = deviceMapper.selectById(deviceId);
            if (devices == null || devices.isEmpty()) {
                return ResponseEntity.status(404).body(createErrorResponse("设备不存在"));
            }

            Device device = devices.get(0);
            
            // 获取设备的传感器数据
            List<Mqttdata> sensorDataList = mqttdataMapper.getDeviceData(deviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deviceId", deviceId);
            response.put("deviceName", device.getName());
            response.put("deviceType", device.getTypeId());
            response.put("roomId", device.getRoomId());
            response.put("homeId", device.getHomeId());
            response.put("lastActiveTime", device.getLastActiveTime());
            response.put("onlineStatus", device.getOnlineStatus());
            response.put("activeStatus", device.getActiveStatus());
            
            if (sensorDataList != null && !sensorDataList.isEmpty()) {
                // 获取最新的数据（列表中的最后一个）
                Mqttdata latestData = sensorDataList.get(sensorDataList.size() - 1);
                response.put("sensorData", latestData);
                response.put("dataCount", sensorDataList.size());
            } else {
                response.put("sensorData", null);
                response.put("dataCount", 0);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取家庭的所有设备传感器数据
     */
    @GetMapping("/home/{homeId}/all")
    public ResponseEntity<Map<String, Object>> getAllHomeSensorData(
            @PathVariable Long homeId) {
        
        try {
            // 获取家庭的所有设备
            List<Device> devices = deviceMapper.selectByHomeId(homeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("homeId", homeId);
            response.put("deviceCount", devices.size());
            response.put("devices", devices);
            
            // 为每个设备获取最新数据
            for (Device device : devices) {
                List<Mqttdata> sensorDataList = mqttdataMapper.getDeviceData(device.getId());
                if (sensorDataList != null && !sensorDataList.isEmpty()) {
                    device.setLastActiveTime(LocalDateTime.now()); // 更新最后活跃时间
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取设备的传感器数据历史记录
     */
    @GetMapping("/device/{deviceId}/history")
    public ResponseEntity<Map<String, Object>> getSensorDataHistory(
            @PathVariable Long deviceId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // 获取设备信息
            List<Device> devices = deviceMapper.selectById(deviceId);
            if (devices == null || devices.isEmpty()) {
                return ResponseEntity.status(404).body(createErrorResponse("设备不存在"));
            }

            // 获取历史数据
            List<Mqttdata> historyData = mqttdataMapper.getDeviceData(deviceId);
            
            // 限制返回的数据量
            if (historyData.size() > limit) {
                historyData = historyData.subList(historyData.size() - limit, historyData.size());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deviceId", deviceId);
            response.put("deviceName", devices.get(0).getName());
            response.put("historyData", historyData);
            response.put("dataCount", historyData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取实时传感器数据（轮询接口）
     */
    @GetMapping("/device/{deviceId}/realtime")
    public ResponseEntity<Map<String, Object>> getRealtimeSensorData(
            @PathVariable Long deviceId) {
        
        try {
            // 获取设备信息
            List<Device> devices = deviceMapper.selectById(deviceId);
            if (devices == null || devices.isEmpty()) {
                return ResponseEntity.status(404).body(createErrorResponse("设备不存在"));
            }

            Device device = devices.get(0);
            
            // 获取最新的传感器数据
            List<Mqttdata> sensorDataList = mqttdataMapper.getDeviceData(deviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("timestamp", LocalDateTime.now());
            response.put("deviceId", deviceId);
            response.put("deviceName", device.getName());
            response.put("onlineStatus", device.getOnlineStatus());
            response.put("activeStatus", device.getActiveStatus());
            response.put("lastActiveTime", device.getLastActiveTime());
            
            if (sensorDataList != null && !sensorDataList.isEmpty()) {
                Mqttdata latestData = sensorDataList.get(sensorDataList.size() - 1);
                response.put("sensorData", latestData);
                response.put("hasData", true);
            } else {
                response.put("sensorData", null);
                response.put("hasData", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("服务器内部错误: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}