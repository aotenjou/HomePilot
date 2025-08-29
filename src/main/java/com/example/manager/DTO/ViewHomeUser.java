package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewHomeUser {
    private Long id;
    private String username;
    private String phone;
    private Integer Role;

    public ViewHomeUser(Long id, String username, String phone, Integer Role) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.Role = Role;
    }
}
