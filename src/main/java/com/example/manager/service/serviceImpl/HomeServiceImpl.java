package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.DTO.MyHomeRole;
import com.example.manager.DTO.ViewHomeDevice;
import com.example.manager.DTO.ViewHomeHome;
import com.example.manager.DTO.ViewHomeUser;
import com.example.manager.entity.*;
import com.example.manager.mapper.*;
import com.example.manager.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HomeServiceImpl extends ServiceImpl<HomeMapper, Home> implements HomeService {

    @Autowired
    private HomeMapper homeMapper;

    @Autowired
    private UserHomeMapper userHomeMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private MyHomeRoleMapper myHomeRoleMapper;

    @Autowired
    private ViewHomeDeviceMapper viewHomeDeviceMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long createHome(Long userId, String name, String address) {
        homeMapper.createHome(name, address);

        Long homeId = homeMapper.getLastInsertId();

        if (homeId != null) {
            userHomeMapper.createUserHome(userId, homeId, UserHome.Role.HOST.getCode());
        }

        return homeId;
    }

    @Override
    public boolean checkUserHome(Long userId, Long homeID) {
        return userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeID) != null;
    }

    @Override
    public boolean checkHome(Long homeId) {
        return homeMapper.selectByHomeId(homeId) != null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteHome(Long homeId) {
        homeMapper.deleteHomeById(homeId);

        userHomeMapper.deleteUserHomeByHomeId(homeId);

        roomMapper.deleteByHomeId(homeId);

        deviceMapper.deleteByHomeId(homeId);
    }

    @Override
    public boolean updateHome(Long homeId, String name, String address) {
        int rowsAffected = homeMapper.updateHome(homeId, name, address);
        return rowsAffected > 0;
    }

    @Override
    public List<Room> getHomeRooms(Long homeId) {
        return roomMapper.selectByHomeId(homeId);
    }

    @Override
    public List<ViewHomeUser> getHomeMembers(Long homeId) {
        return userRoleMapper.getUserRole(homeId);
    }

    @Override
    public Home getHomeInfo(Long homeId) {
        return homeMapper.selectByHomeId(homeId);
    }

    @Override
    public List<Home> getHomeByUserId(Long userId) {
        List<Long> homeIds = userHomeMapper.selectHomeIdByUserId(userId);
        return homeMapper.selectByHomeIds(homeIds);
    }

    @Override
    public List<Device> getHomeDevices(Long homeId) {
        return deviceMapper.selectByHomeId(homeId);
    }

    @Override
    public List<MyHomeRole> getUserHomeByUserId(Long userId) {
        return myHomeRoleMapper.getMyHomeRole(userId);
    }

    @Override
    public boolean updateHomeName(Long homeId, String name) {
        return homeMapper.updateHomeName(homeId, name) > 0;
    }

    @Override
    public boolean updateHomeAddress(Long homeId, String address) {
        return homeMapper.updateHomeAddress(homeId, address) > 0;
    }

    @Override
    public List<Home> searchHomeByName(String name) {
        return homeMapper.searchHomeByPartName(name);
    }

    @Override
    public List<ViewHomeDevice> getHomeDevicesView(Long homeId) {
        return viewHomeDeviceMapper.getHomeDevicesView(homeId);
    }
}