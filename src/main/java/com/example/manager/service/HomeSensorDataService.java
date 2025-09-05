package com.example.manager.service;

import com.example.manager.DTO.HomeAllSensorDataResponse;
import com.example.manager.entity.*;
import com.example.manager.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家庭传感器数据服务类
 * 处理家庭传感器数据的获取和处理逻辑
 */
@Service
public class HomeSensorDataService {
    
    @Autowired
    private DeviceMapper deviceMapper;
    
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    
    @Autowired
    private MqttdataMapper mqttdataMapper;
    
    @Autowired
    private HomeMapper homeMapper;
    
    @Autowired
    private RoomMapper roomMapper;
    
    /**
     * 获取指定家庭的所有传感器数据
     * @param homeId 家庭ID
     * @return 家庭传感器数据响应对象
     */
    public HomeAllSensorDataResponse getHomeAllSensorData(Long homeId) {
        // 获取家庭信息
        Home home = homeMapper.selectById(homeId);
        if (home == null) {
            return null;
        }
        
        // 获取家庭下所有设备
        List<Device> allDevices = deviceMapper.selectByHomeId(homeId);
        if (allDevices == null || allDevices.isEmpty()) {
            return new HomeAllSensorDataResponse(homeId, home.getName(), LocalDateTime.now(), new ArrayList<>());
        }
        
        // 筛选传感器设备（根据设备类型名称判断）
        List<Device> sensorDevices = filterSensorDevices(allDevices);
        
        // 构建传感器设备数据列表
        List<HomeAllSensorDataResponse.SensorDeviceData> sensorDeviceDataList = new ArrayList<>();
        
        for (Device device : sensorDevices) {
            HomeAllSensorDataResponse.SensorDeviceData sensorDeviceData = buildSensorDeviceData(device);
            if (sensorDeviceData != null) {
                sensorDeviceDataList.add(sensorDeviceData);
            }
        }
        
        return new HomeAllSensorDataResponse(homeId, home.getName(), LocalDateTime.now(), sensorDeviceDataList);
    }
    
    /**
     * 筛选传感器设备
     * @param devices 所有设备列表
     * @return 传感器设备列表
     */
    private List<Device> filterSensorDevices(List<Device> devices) {
        List<Device> sensorDevices = new ArrayList<>();
        
        for (Device device : devices) {
            String typeName = deviceTypeMapper.selectNameById(device.getTypeId());
            if (typeName != null && isSensorDevice(typeName)) {
                sensorDevices.add(device);
            }
        }
        
        return sensorDevices;
    }
    
    /**
     * 判断是否为传感器设备
     * @param typeName 设备类型名称
     * @return 是否为传感器设备
     */
    private boolean isSensorDevice(String typeName) {
        // 根据设备类型名称判断是否为传感器
        return typeName.contains("传感器") || 
               typeName.contains("检测") || 
               typeName.contains("监测") ||
               typeName.contains("感应") ||
               typeName.contains("火焰") ||
               typeName.contains("气体") ||
               typeName.contains("温度") ||
               typeName.contains("湿度") ||
               typeName.contains("光照") ||
               typeName.contains("人体") ||
               typeName.contains("烟雾") ||
               typeName.contains("声音") ||
               typeName.contains("震动");
    }
    
    /**
     * 构建传感器设备数据对象
     * @param device 设备对象
     * @return 传感器设备数据对象
     */
    private HomeAllSensorDataResponse.SensorDeviceData buildSensorDeviceData(Device device) {
        try {
            // 获取设备类型名称
            String typeName = deviceTypeMapper.selectNameById(device.getTypeId());
            
            // 获取房间名称
            String roomName = "未知房间";
            if (device.getRoomId() != null) {
                Room room = roomMapper.selectById(device.getRoomId());
                if (room != null) {
                    roomName = room.getName();
                }
            }
            
            // 创建传感器设备数据对象
            HomeAllSensorDataResponse.SensorDeviceData sensorDeviceData = 
                new HomeAllSensorDataResponse.SensorDeviceData(
                    device.getId(),
                    device.getName(),
                    device.getTypeId(),
                    typeName,
                    device.getRoomId(),
                    roomName,
                    device.getOnlineStatus(),
                    device.getLastActiveTime()
                );
            
            // 获取最新传感器数据
            List<Mqttdata> deviceDataList = mqttdataMapper.getDeviceData(device.getId());
            if (deviceDataList != null && !deviceDataList.isEmpty()) {
                // 获取最新数据（假设按时间倒序排列）
                Mqttdata latestMqttData = deviceDataList.get(deviceDataList.size() - 1);
                HomeAllSensorDataResponse.SensorData latestData = 
                    new HomeAllSensorDataResponse.SensorData(
                        latestMqttData.getId(),
                        latestMqttData.getDataValue(),
                        latestMqttData.getTopic(),
                        latestMqttData.getDataTime(),
                        generateStatusDescription(typeName, latestMqttData.getDataValue())
                    );
                sensorDeviceData.setLatestData(latestData);
                
                // 获取历史数据（最近10条）
                List<HomeAllSensorDataResponse.SensorData> historicalData = deviceDataList.stream()
                    .limit(10)
                    .map(mqttData -> new HomeAllSensorDataResponse.SensorData(
                        mqttData.getId(),
                        mqttData.getDataValue(),
                        mqttData.getTopic(),
                        mqttData.getDataTime(),
                        generateStatusDescription(typeName, mqttData.getDataValue())
                    ))
                    .collect(Collectors.toList());
                sensorDeviceData.setHistoricalData(historicalData);
            }
            
            return sensorDeviceData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据传感器类型和数值生成状态描述
     * @param typeName 设备类型名称
     * @param dataValue 数据值
     * @return 状态描述
     */
    private String generateStatusDescription(String typeName, Integer dataValue) {
        if (typeName == null || dataValue == null) {
            return "未知状态";
        }
        
        // 火焰传感器
        if (typeName.contains("火焰") || typeName.contains("火警")) {
            return dataValue > 0 ? "检测到火焰" : "正常";
        }
        
        // 可燃气体传感器
        if (typeName.contains("可燃气体") || typeName.contains("燃气") || typeName.contains("气体")) {
            if (dataValue > 100) {
                return "气体浓度过高";
            } else if (dataValue > 50) {
                return "气体浓度偏高";
            } else {
                return "正常";
            }
        }
        
        // 人体感应传感器
        if (typeName.contains("人体") || typeName.contains("感应")) {
            return dataValue > 0 ? "检测到人体" : "无人";
        }
        
        // 温度传感器
        if (typeName.contains("温度")) {
            if (dataValue > 35) {
                return "温度过高";
            } else if (dataValue < 10) {
                return "温度过低";
            } else {
                return "温度正常";
            }
        }
        
        // 湿度传感器
        if (typeName.contains("湿度")) {
            if (dataValue > 80) {
                return "湿度过高";
            } else if (dataValue < 30) {
                return "湿度过低";
            } else {
                return "湿度正常";
            }
        }
        
        // 默认状态
        return "数值: " + dataValue;
    }
    
    /**
     * 获取指定设备的传感器数据历史记录
     * @param deviceId 设备ID
     * @param limit 限制条数
     * @return 传感器数据列表
     */
    public List<HomeAllSensorDataResponse.SensorData> getDeviceSensorHistory(Long deviceId, Integer limit) {
        List<Mqttdata> mqttDataList = mqttdataMapper.getDeviceData(deviceId);
        if (mqttDataList == null || mqttDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取设备信息用于状态描述
        Device device = deviceMapper.selectById(deviceId);
        String typeName = "";
        if (device != null) {
            typeName = deviceTypeMapper.selectNameById(device.getTypeId());
        }
        
        final String finalTypeName = typeName;
        return mqttDataList.stream()
            .limit(limit != null ? limit : 50)
            .map(mqttData -> new HomeAllSensorDataResponse.SensorData(
                mqttData.getId(),
                mqttData.getDataValue(),
                mqttData.getTopic(),
                mqttData.getDataTime(),
                generateStatusDescription(finalTypeName, mqttData.getDataValue())
            ))
            .collect(Collectors.toList());
    }
}