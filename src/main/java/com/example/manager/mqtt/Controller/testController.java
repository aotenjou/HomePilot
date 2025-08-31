package com.example.manager.mqtt.Controller;

import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceTest;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.DeviceTestMapper;
import com.example.manager.mapper.DeviceTypeMapper;
import com.example.manager.mqtt.Service.MqttConnectService;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class testController {
    @Autowired
    private MqttConnectService mqttConnectService;
    @RequestMapping("/findall")
    public String sendMessage(){
        mqttConnectService.connect();
        return "发送成功";
    }

}