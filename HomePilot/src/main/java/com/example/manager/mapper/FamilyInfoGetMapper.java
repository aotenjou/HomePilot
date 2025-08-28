package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.FamilyProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FamilyInfoGetMapper extends BaseMapper<FamilyProfile> {
}
