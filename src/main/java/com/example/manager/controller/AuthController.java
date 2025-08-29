package com.example.manager.controller;


import com.example.manager.DTO.LoginRequest;
import com.example.manager.entity.User;
import com.example.manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(
        name = "auth接口",
        description = "用户登录、注册、查询功能"
)
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(
            summary = "用户登录",
            description = "用户通过手机号和密码进行登录，成功后返回Token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "用户登录成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "401", description = "密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request,
                                                     @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!authService.checkPhone(request.getPhone())) {
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if(!authService.checkPassword(request.getPhone(), request.getPassword())) {
            response.put("message", "密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Long userId = authService.getUserIdByPhone(request.getPhone());
        response.put("token", authService.getToken(userId.toString()));
        response.put("message", "登录成功");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "用户注册",
            description = "新用户注册，传入用户信息（姓名、手机号、密码等），注册成功返回提示信息"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "手机号已注册"),
            @ApiResponse(responseCode = "500", description = "注册失败")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user,
                                                        @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        if (authService.checkPhone(user.getPhone())) {
            response.put("message", "该手机号已注册");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        authService.createUser(user);
        if(!authService.checkPhone(user.getPhone())) {
            response.put("message", "注册失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "注册成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "通过手机号搜索用户",
            description = "通过手机号查询用户，返回其用户名和用户ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在" )
    })
    @GetMapping("/search-user-by-phone")
    public ResponseEntity<Map<String, Object>> searchUserByPhone(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();

        // 查询用户
        User user = authService.getUserByPhone(phone);

        if (user == null) {
            response.put("status", "error");
            response.put("message", "该手机号未注册");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("status", "success");
        response.put("name", user.getName());
        response.put("userId", user.getId());
        return ResponseEntity.ok(response);
    }
}
