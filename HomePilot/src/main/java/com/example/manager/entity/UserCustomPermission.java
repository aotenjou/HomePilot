package com.example.manager.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_custom_permission")
public class UserCustomPermission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("home_id")
    private Long homeId;

    @TableField("device_id")
    private Long deviceId;

    @TableField("operation_id")
    private Long operationId;

    @TableField("has_permission")
    private Boolean hasPermission;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
