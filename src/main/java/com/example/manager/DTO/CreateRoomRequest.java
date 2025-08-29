package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private Long homeId;
    private String name;
    private String description;

    public CreateRoomRequest(Long homeId,String name, String description) {
        this.homeId = homeId;
        this.name = name;
        this.description = description;
    }
}
