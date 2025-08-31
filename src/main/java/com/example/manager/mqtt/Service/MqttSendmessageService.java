package com.example.manager.mqtt.Service;

public interface MqttSendmessageService {

    void sendMessage(String topic, String message);
}
