package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.MyHomeRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyHomeRoleMapper extends BaseMapper<MyHomeRole> {
    List<MyHomeRole> getMyHomeRole(Long userId);
}
