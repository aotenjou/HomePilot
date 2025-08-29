package com.example.manager.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String phone;
    private String password;

    public LoginRequest() {
        this.phone = phone;
        this.password = password;
    }
}
