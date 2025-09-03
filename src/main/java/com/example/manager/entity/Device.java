package com.example.manager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Device")
public class Device {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("home_id")
    private Long homeId;

    @TableField("room_id")
    private Long roomId;

    @TableField("type_id")
    private Long TypeId;

    @TableField("name")
    private String name;

    @TableField("online_status")
    private Integer onlineStatus;

    @TableField("active_status")
    private Integer activeStatus;

    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    public void setDeviceName(String name) {
        this.name = name;
    }

    @Getter
    public enum OnlineStatus {
        OFFLINE(0, "未在线"),
        ONLINE(1, "在线");
        private final Integer code;
        private final String description;
        OnlineStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getByCode(Integer code) {
            for (OnlineStatus value : OnlineStatus.values()) {
                if (value.code.equals(code)) {
                    return value.description;
                }
            }
            return null;
        }
    }

    @Getter
    public enum ActiveStatus {
        INACTIVE(0, "未激活"),
        ACTIVE(1, "已激活");
        private final Integer code;
        private final String description;
        ActiveStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getByCode(Integer code) {
            for (ActiveStatus status : ActiveStatus.values()) {
                if (status.code.equals(code)) {
                    return status.description;
                }
            }
            return null;
        }
    }
}
