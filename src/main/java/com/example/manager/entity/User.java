package com.example.manager.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("User")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("phone")
    private String phone;

    @TableField("password")
    private String password;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    public String getName() {
        return this.username;
    }
}
