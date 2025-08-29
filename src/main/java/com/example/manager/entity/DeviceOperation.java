package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("device_operation")
public class DeviceOperation implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("device_type_id")
    private Long deviceTypeId;

    @TableField("operation_id")
    private Long operationId;

    @TableField("description")
    private String description;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}