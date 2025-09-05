package com.example.manager.mapper;

import com.example.manager.entity.DeviceType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface DeviceTypeMapper {
    String selectNameById(@Param("id") Long id);

    List<DeviceType> selectAllDeviceTypes();

    DeviceType selectById(@Param("id") Long id);
}
