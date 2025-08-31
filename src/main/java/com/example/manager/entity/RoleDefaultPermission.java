package com.example.manager.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Role_Default_Permission")
public class RoleDefaultPermission implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("role")
    private Integer role;

    @TableField("device_id")
    private Long deviceId;

    @TableField("operation_id")
    private Long operationId;

    @TableField("has_permission")
    private Boolean hasPermission;

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
    }
}
