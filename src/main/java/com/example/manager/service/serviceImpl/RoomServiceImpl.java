package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.entity.Device;
import com.example.manager.entity.Room;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.RoomMapper;
import com.example.manager.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public boolean checkRoom(Long id, Long homeId) {
        if (id == null || homeId == null) {
            log.warn("房间ID或家庭ID为空");
            return false;
        }

        try {
            return roomMapper.selectByRoomIdAndHomeId(id, homeId) != null;
        } catch (Exception e) {
            log.error("检查房间是否存在时发生异常", e);
            throw new RuntimeException("房间检查失败，请稍后再试");
        }
    }

    @Override
    public List<Device> getRoomDevices(Long roomId, Long homeId) {
        return deviceMapper.selectByRoomIdAndHomeId(roomId, homeId);
    }

    @Override
    public Long createRoom(Long homeId, String name) {
        roomMapper.insertRoom(homeId,name);
        return roomMapper.getLastInsertId();
    }

    @Override
    public void deleteRoom(Long id, Long homeId) {
        roomMapper.deleteRoom(id, homeId);
        deviceMapper.deleteByRoomIdAndHomeId(id, homeId);
    }
}
