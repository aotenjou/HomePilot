package com.example.manager.mqtt.Service.Impl;

import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.mqtt.config.MqttProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttSendmessageServiceImpl implements MqttSendmessageService {
    @Autowired
    private MqttProviderConfig providerClient;
    @Override
    public void sendMessage(String topic, String message)
    {
        int qos = 0;
        boolean retained = false;
        providerClient.connect();
        providerClient.publish(qos, retained,topic,message);
    }
}
