package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewHomeRoom {
    private Long id;
    private String name;

    public ViewHomeRoom(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
