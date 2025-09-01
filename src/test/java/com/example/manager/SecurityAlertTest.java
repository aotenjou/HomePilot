package com.example.manager;

import com.example.manager.service.SecurityAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class SecurityAlertTest {

    @Autowired
    private SecurityAlertService securityAlertService;

    @Test
    public void testFireAlert() {
        System.out.println("测试火焰警报功能...");
        securityAlertService.handleFireAlert(1001L, 1L, 1L, 1);
        
        Map<String, Object> status = securityAlertService.getSecurityStatus(1L);
        System.out.println("安全状态: " + status);
    }

    @Test
    public void testGasAlert() {
        System.out.println("测试可燃气体警报功能...");
        securityAlertService.handleGasAlert(1002L, 1L, 1L, 100);
        
        Map<String, Object> status = securityAlertService.getSecurityStatus(1L);
        System.out.println("安全状态: " + status);
    }
}
