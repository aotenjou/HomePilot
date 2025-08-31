package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DeviceOperationRequest {
    private Long deviceId;
    private Long operationId;

    public DeviceOperationRequest(Long deviceId, Long operationId, Long parameter) {
        this.deviceId = deviceId;
        this.operationId = operationId;
    }
}
