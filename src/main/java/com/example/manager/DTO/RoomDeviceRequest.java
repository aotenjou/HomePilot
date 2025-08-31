package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDeviceRequest {
    private Long roomId;
    private Long homeId;

    public RoomDeviceRequest(Long roomId, Long homeId) {
        this.roomId = roomId;
        this.homeId = homeId;
    }
}
