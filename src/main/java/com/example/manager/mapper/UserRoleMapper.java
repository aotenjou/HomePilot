package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.ViewHomeUser;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<ViewHomeUser> {
    List<ViewHomeUser> getUserRole(Long HomeId);

    String getHostName(Long homeId);
}
