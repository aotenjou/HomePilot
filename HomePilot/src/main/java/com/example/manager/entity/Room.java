package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room")
public class Room {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("home_id")
    private Long homeId;

    @TableField("name")
    private String name;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
