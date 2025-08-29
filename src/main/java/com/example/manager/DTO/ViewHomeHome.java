package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewHomeHome {
    private Long id;
    private String name;
    private String address;

    public ViewHomeHome(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
