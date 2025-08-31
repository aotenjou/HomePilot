package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.ViewEnterRequest;
import com.example.manager.entity.EnterRequestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnterRequestRecordMapper extends BaseMapper<EnterRequestRecord> {
    EnterRequestRecord selectByUserIdAndHomeId(@Param("userId") Long userId, @Param("homeId") Long homeId, @Param("status") Integer status);

    Long getLastInsertId();

    List<ViewEnterRequest> selectAllByHomeId(@Param("homeId") Long homeId);

    void updateStatus(@Param("requestId") Long requestId, @Param("status") Integer status);

    Integer selectStatus(@Param("requestId") Long requestId);
}
