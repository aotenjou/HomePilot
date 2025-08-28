package com.example.manager.DTO;

import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceOperation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AddSceneRequest {
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<OperationOfDevice> deviceOperation;

    public AddSceneRequest(String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime, List<OperationOfDevice> deviceOperation) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deviceOperation = deviceOperation;
    }
}
