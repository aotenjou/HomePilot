package com.example.manager.service.serviceImpl;

import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.DeviceTypeMapper;
import com.example.manager.mapper.RoomMapper;
import com.example.manager.entity.Device;
import com.example.manager.entity.Room;
import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.service.SecurityAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityAlertServiceImpl implements SecurityAlertService {
    
    @Autowired
    private DeviceMapper deviceMapper;
    
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    
    @Autowired
    private RoomMapper roomMapper;
    
    @Autowired
    private MqttSendmessageService mqttSendmessageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // 存储安全状态的内存缓存
    private static final ConcurrentHashMap<Long, Map<String, Object>> securityStatusCache = new ConcurrentHashMap<>();
    
    @Override
    public void handleFireAlert(Long deviceId, Long homeId, Long roomId, int value) {
        if (value > 0) { // 检测到火焰
            // 获取房间信息
            Room room = roomMapper.selectById(roomId);
            String roomName = room != null ? room.getName() : "未知房间";
            
            // 创建警报信息
            Map<String, Object> alertInfo = new HashMap<>();
            alertInfo.put("type", "fire");
            alertInfo.put("deviceId", deviceId);
            alertInfo.put("homeId", homeId);
            alertInfo.put("roomId", roomId);
            alertInfo.put("roomName", roomName);
            alertInfo.put("value", value);
            alertInfo.put("timestamp", LocalDateTime.now());
            alertInfo.put("message", "检测到火焰！房间：" + roomName);
            
            // 更新安全状态缓存
            updateSecurityStatus(homeId, "fire", alertInfo);
            
            // 发送警报到前端
            sendAlertToFrontend(homeId, alertInfo);
            
            // 自动关闭可能引起火灾的设备
            autoShutoffDevices(homeId, roomId);
            
        }
    }
    
    @Override
    public void handleGasAlert(Long deviceId, Long homeId, Long roomId, int value) {
        if (value > 50) { // 可燃气体浓度超过阈值
            // 获取房间信息
            Room room = roomMapper.selectById(roomId);
            String roomName = room != null ? room.getName() : "未知房间";
            
            // 创建警报信息
            Map<String, Object> alertInfo = new HashMap<>();
            alertInfo.put("type", "gas");
            alertInfo.put("deviceId", deviceId);
            alertInfo.put("homeId", homeId);
            alertInfo.put("roomId", roomId);
            alertInfo.put("roomName", roomName);
            alertInfo.put("value", value);
            alertInfo.put("timestamp", LocalDateTime.now());
            alertInfo.put("message", "检测到可燃气体泄漏！房间：" + roomName + "，浓度：" + value);
            
            // 更新安全状态缓存
            updateSecurityStatus(homeId, "gas", alertInfo);
            
            // 发送警报到前端
            sendAlertToFrontend(homeId, alertInfo);
            
            // 自动关闭燃气设备
            autoShutoffGasDevices(homeId, roomId);
            
        }
    }
    
    @Override
    public Map<String, Object> getSecurityStatus(Long homeId) {
        return securityStatusCache.getOrDefault(homeId, new HashMap<>());
    }
    
    private void updateSecurityStatus(Long homeId, String alertType, Map<String, Object> alertInfo) {
        Map<String, Object> status = securityStatusCache.computeIfAbsent(homeId, k -> new HashMap<>());
        status.put(alertType + "_alert", alertInfo);
        status.put("last_update", LocalDateTime.now());
    }
    
    private void sendAlertToFrontend(Long homeId, Map<String, Object> alertInfo) {
        try {
            // 通过WebSocket发送实时警报到前端
            String destination = "/topic/security/" + homeId;
            messagingTemplate.convertAndSend(destination, alertInfo);
            
            // 同时通过MQTT发送警报消息
            String alertTopic = "home/" + homeId + "/security/alert";
            String alertMessage = "ALERT:" + alertInfo.get("type") + ":" + alertInfo.get("message");
            mqttSendmessageService.sendMessage(alertTopic, alertMessage);
            
            System.out.println("✅ 安全警报已发送到前端，家庭ID：" + homeId);
        } catch (Exception e) {
            System.err.println("发送警报到前端失败：" + e.getMessage());
        }
    }
    
    private void autoShutoffDevices(Long homeId, Long roomId) {
        try {
            // 获取房间内所有设备
            List<Device> roomDevices = deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
            
            for (Device device : roomDevices) {
                String typeName = deviceTypeMapper.selectNameById(device.getTypeId());
                if (typeName != null && (typeName.contains("灯") || typeName.contains("电器") || typeName.contains("插座"))) {
                    // 关闭设备（operationId = "0" 表示关闭）
                    mqttSendmessageService.sendMessage(device.getId().toString(), "0");
                    System.out.println("自动关闭设备：" + device.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("自动关闭设备失败：" + e.getMessage());
        }
    }
    
    private void autoShutoffGasDevices(Long homeId, Long roomId) {
        try {
            // 获取房间内所有设备
            List<Device> roomDevices = deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
            
            for (Device device : roomDevices) {
                String typeName = deviceTypeMapper.selectNameById(device.getTypeId());
                if (typeName != null && (typeName.contains("燃气") || typeName.contains("炉具") || typeName.contains("热水器"))) {
                    // 关闭燃气设备
                    mqttSendmessageService.sendMessage(device.getId().toString(), "0");
                    System.out.println("自动关闭燃气设备：" + device.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("自动关闭燃气设备失败：" + e.getMessage());
        }
    }
}
