package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Scene")
public class Scene {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("home_id")
    private Long homeId;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Getter
    public enum status {
        DEACTIVATE(0, "未激活"),
        ACTIVATE(1, "激活");

        private final Integer code;
        private final String description;

        status(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
