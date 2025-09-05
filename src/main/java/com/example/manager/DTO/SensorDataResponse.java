package com.example.manager.DTO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 传感器数据响应DTO - 前后端统一的数据格式
 */
@Data
public class SensorDataResponse {
    
    // 基础信息
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    
    // 设备信息
    private Long deviceId;
    private String deviceName;
    private Integer deviceType;
    private Long homeId;
    private Long roomId;
    private Integer onlineStatus;
    private Integer activeStatus;
    private LocalDateTime lastActiveTime;
    
    // 传感器数据
    private SensorData sensorData;
    private Integer dataCount;
    
    /**
     * 传感器数据详情
     */
    @Data
    public static class SensorData {
        private Long id;
        private LocalDateTime dataTime;
        private String topic;
        
        // 环境传感器数据
        private Double temperature;    // 温度 (°C)
        private Double humidity;       // 湿度 (%)
        
        // 光照传感器数据
        private Integer lightA;        // 光照传感器A (0/1)
        private Integer lightB;        // 光照传感器B (0/1)
        private Integer lightC;        // 光照传感器C (0/1)
        
        // 设备状态
        private Integer fanState;      // 风扇状态 (0=关闭, 1=开启)
        
        // 安全传感器数据
        private Integer fireState;     // 火焰状态 (0=正常, 1=检测到火焰)
        private Integer gasState;      // 气体状态 (0=正常, 1=检测到气体)
        
        // 兼容旧格式
        private Integer dataValue;     // 简单数值（向后兼容）
        private String rawJsonData;    // 原始JSON数据
        
        /**
         * 判断是否有完整的传感器数据
         */
        public boolean hasCompleteData() {
            return temperature != null || humidity != null || 
                   lightA != null || lightB != null || lightC != null ||
                   fanState != null || fireState != null || gasState != null;
        }
        
        /**
         * 判断是否有安全警报
         */
        public boolean hasSecurityAlert() {
            return (fireState != null && fireState == 1) || 
                   (gasState != null && gasState == 1);
        }
        
        /**
         * 获取传感器数据摘要
         */
        public String getDataSummary() {
            StringBuilder summary = new StringBuilder();
            if (temperature != null) {
                summary.append("温度:").append(temperature).append("°C ");
            }
            if (humidity != null) {
                summary.append("湿度:").append(humidity).append("% ");
            }
            if (hasSecurityAlert()) {
                summary.append("⚠️安全警报 ");
            }
            return summary.toString().trim();
        }
    }
    
    /**
     * 创建成功响应
     */
    public static SensorDataResponse success(String message) {
        SensorDataResponse response = new SensorDataResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * 创建错误响应
     */
    public static SensorDataResponse error(String message) {
        SensorDataResponse response = new SensorDataResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}



