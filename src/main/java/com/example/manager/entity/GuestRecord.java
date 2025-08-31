package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Guest_Record")
public class GuestRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("home_id")
    private Long homeId;

    @TableField("user_id")
    private Long userId;

    @TableField("record_type")
    private Integer recordType;

    @TableField("record_time")
    private LocalDateTime recordTime;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Getter
    public enum RecordType {
        ENTER(0, "进入"),
        EXIT(1, "离开");
        private final Integer code;
        private final String description;

        RecordType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
    }
}
