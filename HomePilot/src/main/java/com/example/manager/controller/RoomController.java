package com.example.manager.controller;

import com.example.manager.DTO.CreateRoomRequest;
import com.example.manager.entity.Device;
import com.example.manager.entity.Room;
import com.example.manager.service.HomeService;
import com.example.manager.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="房间接口",description="房间增删和获取房间设备ID的接口")
@RestController
@RequestMapping("/home/{homeId}/room")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private HomeService homeService;

    @Operation(summary = "创建房间", description = "根据请求体中的房间信息创建一个新的房间")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "添加房间成功"),
            @ApiResponse(responseCode = "500", description = "添加房间失败")
    })
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Room request,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        Long id = roomService.createRoom(request);
        if(!roomService.checkRoom(id, request.getHomeId())) {
            response.put("message", "添加房间失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "添加房间成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "获取房间或家庭设备列表", description = "当房间ID为0时，获取家庭所有设备；否则获取指定房间的设备")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "设备查看成功"),
            @ApiResponse(responseCode = "404", description = "房间或设备未找到")
    })
    @GetMapping("/device")
    public ResponseEntity<Map<String, Object>> getDevice(@RequestBody Room request,
                                                         @Parameter(description = "家庭ID", required = true)@PathVariable("homeId") Long homeId,
                                                         @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        List<Device> devices;
        if(request.getId() == 0) {
            devices = homeService.getHomeDevices(request.getHomeId());
            if(devices == null) {
                response.put("message", "未发现设备");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("devices", devices);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        if(!roomService.checkRoom(request.getId(), homeId)) {
            response.put("message", "此房间不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        devices = roomService.getRoomDevices(request.getId(), homeId);
        if(devices == null) {
            response.put("message", "未发现设备");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("devices", devices);
        response.put("message", "查看成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "删除房间", description = "根据房间ID删除对应房间")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "家庭不存在该房间"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteRoom(@RequestBody Room request,
                                                          @Parameter(description = "家庭ID", required = true)@PathVariable("homeId") Long homeId,
                                                            @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!roomService.checkRoom(request.getId(), homeId)) {
            response.put("message", "家庭不存在此房间");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        roomService.deleteRoom(request.getId(), homeId);
        if(roomService.checkRoom(request.getId(), homeId)) {
            response.put("message", "删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "删除成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
