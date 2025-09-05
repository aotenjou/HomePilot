package com.example.manager.controller;

import com.example.manager.DTO.*;
import com.example.manager.entity.*;
import com.example.manager.service.HomeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "家庭接口", description = "家庭的创建、查询、删除、修改等操作接口")
@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;


    @Operation(summary = "获取当前用户的所有家庭", description = "通过 userId 查询当前用户加入的所有家庭")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "未找到家庭")
    })
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getHome(@RequestHeader HttpHeaders headers,
                                                       @Parameter(hidden = true)@RequestAttribute("currentUserId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<Home> homes = homeService.getHomeByUserId(userId);
        if(homes.isEmpty()) {
            response.put("message", "未找到家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("homes", homes);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/test-migration")
    public ResponseEntity<Map<String, Object>> testMigration() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 直接查询数据库来测试 createTime 字段
            List<Home> homes = homeService.getHomeByUserId(1L); // 使用用户ID 1进行测试
            response.put("homes", homes);
            response.put("message", "测试成功");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "测试失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "创建家庭", description = "用户创建新的家庭")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> CreateHome(@RequestBody CreateHomeRequest request,
                                                          @Parameter(hidden = true)@RequestAttribute("currentUserId") Long userId,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        Long homeId = homeService.createHome(userId, request.getName(), request.getAddress());
        if(!homeService.checkUserHome(userId, homeId)) {
            response.put("message", "创建失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "创建成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "删除家庭", description = "根据家庭 ID 删除家庭")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    @DeleteMapping("/delete/{homeId}")
    public ResponseEntity<Map<String, Object>> DeleteHome(@PathVariable("homeId") Long homeId,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        homeService.deleteHome(homeId);
        if(homeService.checkHome(homeId)) {
            response.put("message", "删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "删除成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "查看家庭详情", description = "包括房间、成员、设备等")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查看成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在")
    })
    @GetMapping("/view/{homeId}")
    public ResponseEntity<Map<String, Object>> ViewHome(@PathVariable("homeId") Long homeId,
                                                        @Parameter(hidden = true)@RequestAttribute("currentUserId") Long userId,
                                                        @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<Room> rooms = homeService.getHomeRooms(homeId);
        List<ViewHomeUser> viewedUsers = homeService.getHomeMembers(homeId);
        List<ViewHomeDevice> viewedDevices = homeService.getHomeDevicesView(homeId);
        Home home = homeService.getHomeInfo(homeId);

        List<ViewHomeRoom> viewedRooms = rooms.stream()
                .map(room -> new ViewHomeRoom(room.getId(), room.getName()))
                .toList();

        ViewHomeHome viewedHome = new ViewHomeHome(home.getId(), home.getName(), home.getAddress());

        response.put("rooms", viewedRooms);
        response.put("users", viewedUsers);
        response.put("devices", viewedDevices);
        response.put("home", viewedHome);
        response.put("currentUserId", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "获取用户所在的家庭", description = "返回包含角色信息的家庭列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/myHome")
    public ResponseEntity<Map<String, Object>> getMyHome(@RequestAttribute("currentUserId") Long userId,
                                                         @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        List<MyHomeRole> homeList = homeService.getUserHomeByUserId(userId);
        if(homeList.isEmpty()) {
            response.put("home", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response.put("home", homeList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "更新家庭信息", description = "根据 ID 更新家庭名称或地址")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在")
    })
    @PostMapping("/{homeId}/updateName")
    public ResponseEntity<Map<String, Object>> updateHomeName(@PathVariable("homeId") Long homeId,
                                                          @RequestBody UpdateHomeRequest request,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        // 1.检查家庭是否存在
        if (!homeService.checkHome(homeId)) {
            response.put("message", "不存在此家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(request.getName().isEmpty()) {
            response.put("message", "请输入家庭名称");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!homeService.updateHomeName(homeId, request.getName())) {
            response.put("message", "更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "更新成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{homeId}/updateAddress")
    public ResponseEntity<Map<String, Object>> updateHomeAddress(@PathVariable("homeId") Long homeId,
                                                              @RequestBody UpdateHomeRequest request,
                                                              @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "此家庭不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(request.getAddress().isEmpty()) {
            response.put("message", "请输入地址");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!homeService.updateHomeAddress(homeId, request.getAddress())) {
            response.put("message", "更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "更新成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchHome(@RequestParam("keyword") String keyword,
                                                         @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        List<ViewHomeHome> homeList = homeService.searchHomeByName(keyword).stream().map(
                home -> new ViewHomeHome(home.getId(), home.getName(), home.getAddress()))
                .toList();
        if(homeList.isEmpty()) {
            response.put("message", "没有找到符合条件的家庭");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("homes", homeList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}