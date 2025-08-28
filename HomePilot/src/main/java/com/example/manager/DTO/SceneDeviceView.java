package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SceneDeviceView {
    private Long deviceId;
    private String deviceName;
    private Long operationId;
    private String operationName;

    public SceneDeviceView(Long deviceId, String deviceName, Long operationId, String operationName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.operationId = operationId;
        this.operationName = operationName;
    }
}
