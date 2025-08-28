package com.example.manager;


import com.example.manager.entity.Device;
import com.example.manager.mapper.DeviceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ManagerApplicationTests {
    @Test
    void contextLoads() {
        String topic = "home/humiditySensor";
        String payload = "0.5";
    }
    @Autowired
    private DeviceMapper deviceMapper;
    @Test
    public void test() {
        List<Device> list = deviceMapper.findDeviceAll();
        for(Device device : list)
            System.out.println(device.getName());
    }

}
