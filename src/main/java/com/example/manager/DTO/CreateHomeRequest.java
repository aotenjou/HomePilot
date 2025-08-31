package com.example.manager.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHomeRequest {
    private String name;
    private String address;

    public CreateHomeRequest(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
