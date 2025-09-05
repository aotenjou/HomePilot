package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    Device selectById(@Param("id") Long id);

    List<Device> selectByIdList(@Param("idList") List<Long> idList);

    List<Device> selectByHomeId(@Param("homeId") Long homeId);
    
    List<Device> selectDevicesByHomeId(@Param("homeId") Long homeId);

    List<Device> selectByRoomIdAndHomeId(@Param("roomId") Long roomId, @Param("homeId") Long homeId);

    List<Device> selectByUserIdAndHomeId(@Param("userId") Long userId, @Param("homeId") Long homeId);

    int updateLastActiveTime(@Param("id") Long id, @Param("time") LocalDateTime time);

    LocalDateTime selectLastActiveTime(@Param("id") Long id);

    void deleteByHomeId(@Param("homeId") Long homeId);

    Integer selectOnlineStatus(@Param("id") Long id);

    Integer selectActiveStatus(@Param("id") Long id);

    List<Device> findDeviceAll();

    void deleteByRoomIdAndHomeId(@Param("roomId") Long roomId, @Param("homeId") Long homeId);

    List<Long> selectAllId();
//    name, ip_address,home_id, room_id, type_id, online_status, active_status, last_active_time
    void insertDevice(@Param("name") String name,@Param("ipAddress")String ipAddress, @Param("homeId") Long homeId ,@Param("roomId")Long roomId, @Param("typeId")Long typeId,@Param("onlineStatus")Integer onlineStatus,@Param("activeStatus")Integer activeStatus,@Param("lastActiveTime")LocalDateTime lastActiveTime);

    Long getLastInsertId();
}