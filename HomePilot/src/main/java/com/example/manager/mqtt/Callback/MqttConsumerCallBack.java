package com.example.manager.mqtt.Callback;


import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.MqttdataMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class MqttConsumerCallBack implements MqttCallback{

    @Autowired
    private MqttdataMapper mqttdataMapper;

    @Autowired
    private DeviceMapper deviceMapper;
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
    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println(String.format("接收消息成功"));
    }
}