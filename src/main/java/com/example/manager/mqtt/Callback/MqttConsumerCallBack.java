package com.example.manager.mqtt.Callback;


import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.DeviceTypeMapper;
import com.example.manager.mapper.MqttdataMapper;
import com.example.manager.entity.Device;
import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.service.SecurityAlertService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class MqttConsumerCallBack implements MqttCallback{

    @Autowired
    private MqttdataMapper mqttdataMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private MqttSendmessageService mqttSendmessageService;

    @Autowired
    private SecurityAlertService securityAlertService;


    /*
     * 客户端断开连接的回调
     */

    public static MqttConsumerCallBack pushCallback;

    @PostConstruct
    public void init() {
        System.out.println("=== MqttConsumerCallBack 初始化开始 ===");
        pushCallback = this;
        pushCallback.mqttdataMapper = this.mqttdataMapper;
        pushCallback.deviceMapper = this.deviceMapper;
        pushCallback.deviceTypeMapper = this.deviceTypeMapper;
        pushCallback.mqttSendmessageService = this.mqttSendmessageService;
        pushCallback.securityAlertService = this.securityAlertService;
        System.out.println("=== MqttConsumerCallBack 初始化完成 ===");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.err.println("=== MQTT连接丢失 ===");
        System.err.println("错误信息: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public MqttConsumerCallBack() {
        System.out.println("=== MqttConsumerCallBack 构造函数调用 ===");
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String feedback = new String(message.getPayload());
        LocalDateTime localtime = LocalDateTime.now();
        
        System.out.println("=== 收到MQTT消息 ===");
        System.out.println("主题: " + topic);
        System.out.println("消息内容: " + feedback);
        System.out.println("QoS: " + message.getQos());
        System.out.println("保留消息: " + message.isRetained());
        System.out.println("接收时间: " + localtime);

        try {
            // 尝试解析JSON格式消息
            if (feedback.trim().startsWith("{")) {
                System.out.println("检测到JSON格式消息，开始解析...");
                parseJsonMessage(feedback, topic, localtime);
            } else {
                System.out.println("检测到简单格式消息，开始解析...");
                // 兼容原有的简单格式消息
                parseSimpleMessage(feedback, topic, localtime);
            }
            System.out.println("=== 消息处理完成 ===");
        } catch (Exception e) {
            System.err.println("=== 消息解析失败 ===");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析JSON格式的硬件消息
     */
    private void parseJsonMessage(String jsonMessage, String topic, LocalDateTime localtime) throws Exception {
        // 使用Jackson或Gson解析JSON
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(jsonMessage);
        
        String deviceMac = jsonNode.get("device").asText();
        System.out.println("设备MAC地址: " + deviceMac);
        
        // 根据MAC地址查找设备ID（需要实现这个方法）
        Long deviceId = findDeviceIdByMac(deviceMac);
        if (deviceId == null) {
            System.out.println("未找到设备ID，MAC: " + deviceMac);
            return;
        }
        
        // 解析各种传感器数据
        double temperature = jsonNode.get("Wendu").asDouble();
        double humidity = jsonNode.get("Shidu").asDouble();
        int lightA = jsonNode.get("LightA").asInt();
        int lightB = jsonNode.get("LightB").asInt();
        int lightC = jsonNode.get("LightC").asInt();
        int fanState = jsonNode.get("FanState").asInt();
        int fireState = jsonNode.get("FireState").asInt();
        int gasState = jsonNode.get("GasState").asInt();
        
        System.out.println("解析数据 - 温度: " + temperature + "°C, 湿度: " + humidity + "%, 火焰: " + fireState + ", 气体: " + gasState);

        // 存储完整的传感器数据
        try {
            System.out.println("开始存储传感器数据到数据库...");
            pushCallback.mqttdataMapper.insertSensorData(
                deviceId, localtime, topic,
                temperature, humidity, lightA, lightB, lightC,
                fanState, fireState, gasState
            );
            System.out.println("传感器数据存储成功");
            
            pushCallback.deviceMapper.updateLastActiveTime(deviceId, localtime);
            System.out.println("设备最后活跃时间更新成功");
            
            // 执行自动化逻辑
            executeAutomationLogic(deviceId, temperature, humidity, fireState, gasState, lightA, lightB, lightC, fanState);
            
            System.out.println("JSON消息解析完成，数据已存储到数据库");
        } catch (Exception e) {
            System.err.println("数据存储失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析简单格式的消息（兼容原有格式）
     */
    private void parseSimpleMessage(String message, String topic, LocalDateTime localtime) throws Exception {
        // 截取字符串前四位作为设备ID
        String deviceIdStr = message.substring(0, 4);
        Long deviceId = Long.parseLong(deviceIdStr);
        // 截取字符串第六位到末尾作为值
        String valueStr = message.substring(5);
        int value = Integer.parseInt(valueStr);
        
        try {
            System.out.println("开始存储简单格式数据到数据库...");
            pushCallback.mqttdataMapper.InsertMqttdata(deviceId, localtime, topic, value);
            System.out.println("简单格式数据存储成功");
            
            pushCallback.deviceMapper.updateLastActiveTime(deviceId, localtime);
            System.out.println("设备最后活跃时间更新成功");
            
            System.out.println("简单格式 - 设备ID: " + deviceId + ", 值: " + value);
        } catch (Exception e) {
            System.err.println("简单格式数据存储失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据MAC地址查找设备ID
     */
    private Long findDeviceIdByMac(String deviceMac) {
        try {
            // 由于数据库中没有MAC地址字段，我们使用一个映射策略
            // 可以根据MAC地址的特定模式来匹配设备
            System.out.println("查找设备MAC: " + deviceMac);
            
            // 方法1: 根据MAC地址模式匹配
            // 例如：DC:11:31:EA:21:4E 可以映射到特定的设备ID
            if (deviceMac.equals("DC:11:31:EA:21:4E")) {
                return 1L; // 映射到设备ID 1
            } else if (deviceMac.equals("70:11:31:E2:0E:0E")) {
                return 1L; // 映射到设备ID 1
            }
            
            // 方法2: 如果没有找到匹配的MAC，返回第一个可用的设备ID
            List<Device> devices = pushCallback.deviceMapper.selectList(null);
            if (devices != null && !devices.isEmpty()) {
                Device firstDevice = devices.get(0);
                System.out.println("使用默认设备: " + firstDevice.getName() + " (ID: " + firstDevice.getId() + ")");
                return firstDevice.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1L; // 返回默认设备ID
    }

    /**
     * 执行自动化逻辑
     */
    private void executeAutomationLogic(Long deviceId, double temperature, double humidity, 
                                      int fireState, int gasState, int lightA, int lightB, int lightC, int fanState) {
        try {
            List<Device> devices = pushCallback.deviceMapper.selectById(deviceId);
            if (devices == null || devices.isEmpty()) {
                return;
            }
            Device sensorDevice = devices.get(0);
            Long roomId = sensorDevice.getRoomId();
            Long homeId = sensorDevice.getHomeId();

            // 安全传感器检测逻辑
            if (fireState == 1) {
                pushCallback.securityAlertService.handleFireAlert(deviceId, homeId, roomId, fireState);
                return;
            }
            
            if (gasState == 1) {
                pushCallback.securityAlertService.handleGasAlert(deviceId, homeId, roomId, gasState);
                return;
            }

            // 温度自动控制：温度超过30度时，打开风扇
            if (temperature > 30 && fanState == 0) {
                List<Device> roomDevices = pushCallback.deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
                for (Device d : roomDevices) {
                    String tName = pushCallback.deviceTypeMapper.selectNameById(d.getTypeId());
                    if (tName != null && tName.contains("风扇")) {
                        pushCallback.mqttSendmessageService.sendMessage(d.getId().toString(), "1");
                        System.out.println("温度过高，自动开启风扇: " + d.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println(String.format("接收消息成功"));
    }
}