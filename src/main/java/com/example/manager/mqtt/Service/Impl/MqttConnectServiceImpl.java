package com.example.manager.mqtt.Service.Impl;


import com.example.manager.entity.DeviceTest;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.DeviceTestMapper;
import com.example.manager.mqtt.config.MqttConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.manager.mqtt.Service.MqttConnectService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MqttConnectServiceImpl implements MqttConnectService {

    @Autowired
    private MqttConsumerConfig client;
    @Autowired
    private DeviceTestMapper devicetestMapper;

    @Autowired
    private DeviceMapper deviceMapper;


    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Override
    public void connect() {
        client.connect();
        List<DeviceTest> list = devicetestMapper.selectAll();
        int qos = 0;  // 设置消息质量等级

        for (DeviceTest test : list) {
            String topic = test.getTopic();  // 获取设备的 topic
            if (topic != null && !topic.isEmpty()) {
                    client.subscribe(topic, qos);// 订阅该 topic
            }
        }
        /*
        scheduler.schedule(() -> {
            disConnect(); // 5秒后执行断开连接
        }, 10, TimeUnit.SECONDS);*/
        //client.disConnect();
    }

    @Override
    public void disConnect() {
        client.disConnect();
    }
}