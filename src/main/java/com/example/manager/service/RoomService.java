package com.example.manager.service;

import com.example.manager.entity.Device;
import com.example.manager.entity.Room;

import java.util.List;

public interface RoomService {
    boolean checkRoom(Long id, Long homeId);

    List<Device> getRoomDevices(Long roomId, Long homeId);

    Long createRoom(Long homeId, String name);

    void deleteRoom(Long id, Long homeId);

    List<Room> getRoom(Long homeId);
}
