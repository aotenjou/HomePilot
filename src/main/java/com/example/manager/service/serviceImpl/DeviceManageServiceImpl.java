package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.entity.Device;
import com.example.manager.entity.DeviceType;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.DeviceTypeMapper;
import com.example.manager.service.DeviceManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceManageServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceManageService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Override
    public Long addDevice(Device device) {
        deviceMapper.insertDevice(device.getName(), device.getIpAddress(), device.getHomeId(), device.getRoomId(), device.getTypeId(), device.getOnlineStatus(), device.getActiveStatus(), LocalDateTime.now(), false);
        return deviceMapper.getLastInsertId();
    }
    @Override
    public List<Device> getDevicesByHomeID (Long homeId){
        return deviceMapper.selectByHomeId(homeId);
    }
    @Override
    public boolean updateDevice(Device Device) {
        return this.updateById(Device);
    }
    @Override
    public boolean removeDevice(Long id) {
        return this.removeById(id);
    }


    @Override
    public boolean updateLastActiveTime(Long id, LocalDateTime time) {
        int rows = deviceMapper.updateLastActiveTime(id, time);
        return rows > 0;
    }

    @Override
    public List<Device> getDevicesByUserAndHome(Long userId, Long homeId) {
        return deviceMapper.selectByUserIdAndHomeId(userId, homeId);
    }

    @Override
    public List<DeviceType> getAllDeviceTypes() {
        return deviceTypeMapper.selectAllDeviceTypes();
    }


}
