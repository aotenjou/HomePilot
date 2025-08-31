package com.example.manager.mqtt.Controller;

import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.mqtt.config.MqttProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SendController {
    @Autowired
    private MqttSendmessageService providerClient;

    @RequestMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(){
        providerClient.sendMessage("lxy","3");
        return "发送成功";
    }
}