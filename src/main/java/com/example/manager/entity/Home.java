package com.example.manager.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("home")
public class Home {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("address")
    private String address;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
