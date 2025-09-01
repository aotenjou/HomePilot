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
        System.out.println("MqttConsumerCallBack init");
        pushCallback = this;
        pushCallback.mqttdataMapper = this.mqttdataMapper;
        pushCallback.deviceMapper = this.deviceMapper;
        pushCallback.deviceTypeMapper = this.deviceTypeMapper;
        pushCallback.mqttSendmessageService = this.mqttSendmessageService;
        pushCallback.securityAlertService = this.securityAlertService;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("与服务器断开连接");
    }

    public MqttConsumerCallBack() {
        System.out.println("MqttConsumerCallBack constructed");
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //System.out.println("asdasd");

        String feedback = new String(message.getPayload());
        System.out.println(feedback);

        LocalDateTime localtime = LocalDateTime.now();
        System.out.println(localtime);


        // 截取字符串前四位作为设备ID
        String deviceIdStr = feedback.substring(0, 4);
        Long deviceId = Long.parseLong(deviceIdStr);
        System.out.println(deviceId);
        // 截取字符串第六位到末尾作为值
        String valueStr = feedback.substring(5);
        int value = Integer.parseInt(valueStr);
        pushCallback.mqttdataMapper.InsertMqttdata(deviceId,  localtime,topic, value);
        pushCallback.deviceMapper.updateLastActiveTime(deviceId, localtime);
        System.out.println(deviceId);
        System.out.println(value);
        System.out.println(localtime);
        System.out.println(String.format("接收消息主题 : %s", topic));
        System.out.println(String.format("接收消息Qos : %d", message.getQos()));
        System.out.println(String.format("接收消息内容 : %s", new String(message.getPayload())));
        System.out.println(String.format("接收消息retained : %b", message.isRetained()));

        // 基于传感器的自动化逻辑
        try {
            List<Device> devices = pushCallback.deviceMapper.selectById(deviceId);
            if (devices == null || devices.isEmpty()) {
                return;
            }
            Device sensorDevice = devices.get(0);
            Long roomId = sensorDevice.getRoomId();
            Long homeId = sensorDevice.getHomeId();
            Long typeId = sensorDevice.getTypeId();
            String typeName = pushCallback.deviceTypeMapper.selectNameById(typeId);

            // 安全传感器检测逻辑
            if (typeName != null) {
                // 火焰传感器检测
                if (typeName.contains("火焰") || typeName.contains("火警")) {
                    pushCallback.securityAlertService.handleFireAlert(deviceId, homeId, roomId, value);
                    return;
                }
                
                // 可燃气体传感器检测
                if (typeName.contains("可燃气体") || typeName.contains("燃气") || typeName.contains("气体")) {
                    pushCallback.securityAlertService.handleGasAlert(deviceId, homeId, roomId, value);
                    return;
                }
            }

            // 人体感应：检测到人体时，开灯（operationId = "1" TurnOn）
            if (typeName != null && typeName.contains("人体") && value > 0) {
                List<Device> roomDevices = pushCallback.deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
                for (Device d : roomDevices) {
                    String tName = pushCallback.deviceTypeMapper.selectNameById(d.getTypeId());
                    if (tName != null && tName.contains("灯")) {
                        pushCallback.mqttSendmessageService.sendMessage(d.getId().toString(), "1");
                    }
                }
                return;
            }

            // 温度传感器：温度超过30度时，打开风扇（operationId = "1" TurnOn）
            if (typeName != null && typeName.contains("温度") && value > 30) {
                List<Device> roomDevices = pushCallback.deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
                for (Device d : roomDevices) {
                    String tName = pushCallback.deviceTypeMapper.selectNameById(d.getTypeId());
                    if (tName != null && tName.contains("风扇")) {
                        pushCallback.mqttSendmessageService.sendMessage(d.getId().toString(), "1");
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