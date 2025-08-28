package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private String name;
    private String description;

    public CreateRoomRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
