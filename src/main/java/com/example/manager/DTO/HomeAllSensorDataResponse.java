package com.example.manager.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭所有传感器数据响应类
 * 用于返回指定家庭下所有传感器设备的最新数据信息
 */
@Data
public class HomeAllSensorDataResponse {
    
    /**
     * 家庭ID
     */
    private Long homeId;
    
    /**
     * 家庭名称
     */
    private String homeName;
    
    /**
     * 数据获取时间
     */
    private LocalDateTime dataTime;
    
    /**
     * 传感器设备数据列表
     */
    private List<SensorDeviceData> sensorDevices;
    
    /**
     * 传感器设备数据内部类
     */
    @Getter
    @Setter
    public static class SensorDeviceData {
        /**
         * 设备ID
         */
        private Long deviceId;
        
        /**
         * 设备名称
         */
        private String deviceName;
        
        /**
         * 设备类型ID
         */
        private Long typeId;
        
        /**
         * 设备类型名称
         */
        private String typeName;
        
        /**
         * 所在房间ID
         */
        private Long roomId;
        
        /**
         * 所在房间名称
         */
        private String roomName;
        
        /**
         * 设备在线状态 (0-离线, 1-在线)
         */
        private Integer onlineStatus;
        
        /**
         * 最后活跃时间
         */
        private LocalDateTime lastActiveTime;
        
        /**
         * 最新传感器数据
         */
        private SensorData latestData;
        
        /**
         * 历史传感器数据列表（可选，用于趋势分析）
         */
        private List<SensorData> historicalData;
        
        public SensorDeviceData() {}
        
        public SensorDeviceData(Long deviceId, String deviceName, Long typeId, String typeName, 
                               Long roomId, String roomName, Integer onlineStatus, 
                               LocalDateTime lastActiveTime) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.typeId = typeId;
            this.typeName = typeName;
            this.roomId = roomId;
            this.roomName = roomName;
            this.onlineStatus = onlineStatus;
            this.lastActiveTime = lastActiveTime;
        }
    }
    
    /**
     * 传感器数据内部类
     */
    @Getter
    @Setter
    public static class SensorData {
        /**
         * 数据ID
         */
        private Long id;
        
        /**
         * 数据值
         */
        private Integer dataValue;
        
        /**
         * 数据主题
         */
        private String topic;
        
        /**
         * 数据记录时间
         */
        private LocalDateTime dataTime;
        
        /**
         * 数据状态描述（如：正常、警报等）
         */
        private String statusDescription;
        
        public SensorData() {}
        
        public SensorData(Long id, Integer dataValue, String topic, LocalDateTime dataTime) {
            this.id = id;
            this.dataValue = dataValue;
            this.topic = topic;
            this.dataTime = dataTime;
        }
        
        public SensorData(Long id, Integer dataValue, String topic, LocalDateTime dataTime, String statusDescription) {
            this.id = id;
            this.dataValue = dataValue;
            this.topic = topic;
            this.dataTime = dataTime;
            this.statusDescription = statusDescription;
        }
    }
    
    public HomeAllSensorDataResponse() {}
    
    public HomeAllSensorDataResponse(Long homeId, String homeName, LocalDateTime dataTime, List<SensorDeviceData> sensorDevices) {
        this.homeId = homeId;
        this.homeName = homeName;
        this.dataTime = dataTime;
        this.sensorDevices = sensorDevices;
    }
}