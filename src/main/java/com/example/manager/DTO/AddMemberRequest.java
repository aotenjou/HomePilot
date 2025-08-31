package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddMemberRequest {
    private Long homeId;  // 家庭ID
    private Long userId;  // 用户ID，来自手机号查找结果
    private Integer role; // 角色，必须填写

    public AddMemberRequest() {}
}