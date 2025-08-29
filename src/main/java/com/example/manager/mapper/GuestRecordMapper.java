package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.GuestRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuestRecordMapper extends BaseMapper<GuestRecord> {
    void insertRecord(GuestRecord record);
}
