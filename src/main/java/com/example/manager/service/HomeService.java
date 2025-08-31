package com.example.manager.service;

import com.example.manager.DTO.MyHomeRole;
import com.example.manager.DTO.ViewHomeDevice;
import com.example.manager.DTO.ViewHomeHome;
import com.example.manager.DTO.ViewHomeUser;
import com.example.manager.entity.*;

import java.util.List;

public interface HomeService {
    Long createHome(Long userId, String name, String address);

    boolean checkUserHome(Long userId, Long homeId);

    boolean checkHome(Long homeId);

    void deleteHome(Long homeId);

    List<Room> getHomeRooms(Long homeId);

    List<ViewHomeUser> getHomeMembers(Long homeId);

    Home getHomeInfo(Long homeId);

    List<Home> getHomeByUserId(Long userId);

    List<Device> getHomeDevices(Long homeId);

    // 更新房间name和Address
    boolean updateHome(Long homeId, String name, String address);

    List<MyHomeRole> getUserHomeByUserId(Long userId);

    boolean updateHomeName(Long homeId, String name);

    boolean updateHomeAddress(Long homeId, String address);

    List<Home> searchHomeByName(String name);

    List<ViewHomeDevice> getHomeDevicesView(Long homeId);
}
