package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    List<Room> selectByHomeId(@Param("homeId") Long homeId);

    void deleteByHomeId(@Param("homeId") Long homeId);

    Room selectByRoomIdAndHomeId(@Param("id") Long id, @Param("homeId") Long homeId);

    void insertRoom(@Param("room") Room room);

    Long getLastInsertId();

    void deleteRoom(@Param("id") Long id, @Param("homeId") Long homeId);

    String selectNameById(@Param("id") Long id);
}
