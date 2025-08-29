package com.example.manager.DTO;

import lombok.Data;

@Data
public class AddMemberByPhoneRequest {
    private Long homeId;
    private String phone;
    private String role; // 可选，默认可填"member"
}

