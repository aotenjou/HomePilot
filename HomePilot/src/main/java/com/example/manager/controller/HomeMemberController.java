package com.example.manager.controller;

import com.example.manager.DTO.AddMemberByPhoneRequest;
import com.example.manager.DTO.AddMemberRequest;
import com.example.manager.entity.User;
import com.example.manager.service.HomeMemberService;
import com.example.manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "家庭成员接口", description = "用于管理家庭中的成员信息")
@RestController
@RequestMapping("/home/member")
public class HomeMemberController {

    @Autowired
    private HomeMemberService homeMemberService;

    @Autowired
    private AuthService authService;


    @Operation(
            summary = "添加家庭成员",
            description = "通过用户ID和家庭ID将指定用户添加为家庭成员"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "409", description = "该用户已是家庭成员"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content)
    })
    // 这里先调用auth中的selectbyphone返回user的信息，再add成员
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMember(@RequestBody AddMemberRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 判断是否已是该家庭成员
            boolean exists = homeMemberService.isUserInHome(request.getUserId(), request.getHomeId());
            if (exists) {
                response.put("message", "该用户已是家庭成员");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // 添加家庭成员
            homeMemberService.addMemberToHome(request.getHomeId(), request.getUserId(), request.getRole());
            response.put("message", "添加家庭成员成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "添加家庭成员失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    // 直接通过手机号添加信息
//    @PostMapping("/add-by-phone")
//    public ResponseEntity<Map<String, Object>> addMemberByPhone(@RequestBody AddMemberByPhoneRequest request) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            User user = authService.getUserByPhone(request.getPhone());
//            if (user == null) {
//                response.put("message", "手机号未注册");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            boolean exists = homeMemberService.isUserInHome(user.getId(), request.getHomeId());
//            if (exists) {
//                response.put("message", "该用户已是家庭成员");
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//            }
//
//            String role = request.getRole() != null ? request.getRole() : "member";
//            homeMemberService.addMemberToHome(request.getHomeId(), user.getId(), role);
//
//            response.put("message", "添加家庭成员成功");
//            response.put("userId", user.getId());
//            response.put("username", user.getUsername());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("message", "添加失败：" + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
}

