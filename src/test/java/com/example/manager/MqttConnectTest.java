package com.example.manager;

import com.example.manager.mqtt.Service.MqttConnectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MqttConnectTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MqttConnectService client;
    @Test
    public void test() {
        client.connect();
    }
}
