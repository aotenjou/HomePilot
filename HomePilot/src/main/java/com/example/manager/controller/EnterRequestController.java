package com.example.manager.controller;

import com.example.manager.DTO.HandleRequest;
import com.example.manager.DTO.ViewEnterRequest;
import com.example.manager.entity.EnterRequestRecord;
import com.example.manager.service.EnterHomeRequestService;
import com.example.manager.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="家庭申请接口",description="用户申请加入家庭的相关操作")
@RestController
@RequestMapping("/home/{homeId}/request")
public class EnterRequestController {
    @Autowired
    private EnterHomeRequestService enterHomeRequestService;

    @Autowired
    private HomeService homeService;

    @Operation(
            summary = "发起加入家庭申请",
            description = "用户向指定家庭发起加入请求，前提是该家庭存在，并且用户未加入或未申请过。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "申请成功/已发送过申请"),
            @ApiResponse(responseCode = "404", description = "该家庭不存在"),
            @ApiResponse(responseCode = "500", description = "申请失败")
    })
    @PostMapping("/put")
    public ResponseEntity<Map<String, Object>> putRequest(@PathVariable("homeId") Long homeId,
                                                          @RequestAttribute("currentUserId") Long userId,
                                                          @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();

        if(!homeService.checkHome(homeId)) {
            response.put("message", "该家庭不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if(homeService.checkUserHome(userId, homeId)) {
            response.put("message", "已加入该家庭");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if(enterHomeRequestService.checkHomeEnter(homeId, userId, 0)) {
            response.put("message", "已发送过申请");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        Long requestId = enterHomeRequestService.createEnterRequest(homeId, userId);
        if(!enterHomeRequestService.checkRecord(requestId)) {
            response.put("message", "申请失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "申请成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/receive")
    public ResponseEntity<Map<String, Object>> receiveRequest(@RequestAttribute("currentUserId") Long userId,
                                                                  @PathVariable("homeId") Long homeId,
                                                                  @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!homeService.checkHome(homeId)) {
            response.put("message", "该家庭不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<ViewEnterRequest> requestList = enterHomeRequestService.getAllEnterRequest(homeId);
        if(requestList.isEmpty()) {
            response.put("message", "没有加入家庭的申请");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("requests", requestList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("receive/handle")
    public ResponseEntity<Map<String, Object>> handleRequest(@RequestBody HandleRequest request,
                                                              @PathVariable("homeId") Long homeId,
                                                              @RequestAttribute("currentUserId") Long userId,
                                                              @RequestHeader HttpHeaders headers) {
        Map<String, Object> response = new HashMap<>();
        if(!enterHomeRequestService.checkRecord(request.getRequestId())) {
            response.put("message", "申请不存在或已取消");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        enterHomeRequestService.handleEnterRequest(request.getUserId(), homeId, request.getRequestId(), request.getStatus());

        if(!enterHomeRequestService.checkRequestStatus(request.getRequestId()).equals(EnterRequestRecord.Status.APPROVED.getCode())) {
            response.put("message", "处理失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "处理成功");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
