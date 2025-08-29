package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("device_in_scene")
public class DeviceInScene {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("device_id")
    private Long deviceId;

    @TableField("device_operation_id")
    private Long deviceOperationId;

    @TableField("scene_id")
    private Long sceneId;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
