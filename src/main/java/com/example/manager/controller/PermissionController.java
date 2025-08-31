package com.example.manager.controller;

import com.example.manager.entity.UserCustomPermission;
import com.example.manager.entity.UserHome;
import com.example.manager.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "权限管理接口", description = "用户权限的添加和取消相关接口")
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "添加用户权限", description = "为用户添加自定义权限，权限ID需唯一")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "添加权限成功"),
            @ApiResponse(responseCode = "409", description = "该用户已拥有此权限"),
            @ApiResponse(responseCode = "500", description = "添加权限失败")
    })
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addPermission(@RequestBody UserCustomPermission request,
                                                             @RequestHeader HttpHeaders headers){
        Map<String, Object> response = new HashMap<>();
        if(permissionService.checkPermission(request.getId())) {
            response.put("message", "该用户已拥有此权限");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        permissionService.createUserCustomPermission(request);
        if(!permissionService.checkPermission(request.getId())) {
            response.put("message", "添加权限失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "添加权限成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "取消用户权限", description = "取消用户的指定权限")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取消权限成功"),
            @ApiResponse(responseCode = "409", description = "该用户未拥有此权限"),
            @ApiResponse(responseCode = "500", description = "取消权限失败")
    })
    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelPermission(@RequestBody UserCustomPermission request,
                                                               @RequestHeader HttpHeaders headers){
        Map<String, Object> response = new HashMap<>();
        if(!permissionService.checkPermission(request.getId())) {
            response.put("message", "该用户未拥有此权限");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        permissionService.cancelPermission(request.getId());
        if(permissionService.checkPermission(request.getId())) {
            response.put("message", "取消权限失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "取消权限成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
