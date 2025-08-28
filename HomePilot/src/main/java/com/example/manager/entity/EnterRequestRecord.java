package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("enter_request_record")
public class EnterRequestRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("home_id")
    private Long homeId;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private Integer status;

    @TableField("record_time")
    private LocalDateTime recordTime;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Getter
    public enum Status {
        WAITING(0, "等待处理"),
        APPROVED(1, "已通过"),
        REJECTED(2, "被拒绝");
        private final Integer code;
        private final String description;
        Status(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
