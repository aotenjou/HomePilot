package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewHomeDevice {
    private Long id;
    private Long typeId;
    private String name;
    private Long operationId;
    private String operationName;
    private Long roomId;
    private Long homeId;

    public ViewHomeDevice(Long id, Long typeId, String name, Long operationId, String operationName, Long roomId, Long homeId) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.operationId = operationId;
        this.operationName = operationName;
        this.roomId = roomId;
        this.homeId = homeId;
    }
}
