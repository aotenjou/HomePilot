package com.example.manager.controller;

import com.example.manager.service.SecurityAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "*")
public class SecurityController {
    
    @Autowired
    private SecurityAlertService securityAlertService;
    
    /**
     * 获取家庭安全状态
     * @param homeId 家庭ID
     * @return 安全状态信息
     */
    @GetMapping("/status/{homeId}")
    public ResponseEntity<Map<String, Object>> getSecurityStatus(@PathVariable Long homeId) {
        try {
            Map<String, Object> status = securityAlertService.getSecurityStatus(homeId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 手动触发火焰警报测试
     * @param homeId 家庭ID
     * @param roomId 房间ID
     * @return 测试结果
     */
    @PostMapping("/test/fire/{homeId}/{roomId}")
    public ResponseEntity<String> testFireAlert(@PathVariable Long homeId, @PathVariable Long roomId) {
        try {
            securityAlertService.handleFireAlert(9999L, homeId, roomId, 1);
            return ResponseEntity.ok("火焰警报测试已触发");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("测试失败：" + e.getMessage());
        }
    }
    
    /**
     * 手动触发可燃气体警报测试
     * @param homeId 家庭ID
     * @param roomId 房间ID
     * @return 测试结果
     */
    @PostMapping("/test/gas/{homeId}/{roomId}")
    public ResponseEntity<String> testGasAlert(@PathVariable Long homeId, @PathVariable Long roomId) {
        try {
            securityAlertService.handleGasAlert(9999L, homeId, roomId, 100);
            return ResponseEntity.ok("可燃气体警报测试已触发");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("测试失败：" + e.getMessage());
        }
    }
}
