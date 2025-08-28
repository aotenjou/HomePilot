package com.example.manager.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_home")
public class UserHome implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("home_id")
    private Long homeId;

    @TableField("role")
    private Integer role;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Getter
    public enum Role {
        HOST(0, "房主"),
        MEMBER(1, "家庭成员"),
        GUEST(2, "访客");

        private final Integer code;
        private final String description;

        Role(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getByCode(Integer code) {
            for (Role role : Role.values()) {
                if (role.code.equals(code)) {
                    return role.description;
                }
            }
            return null;
        }
    }
}
