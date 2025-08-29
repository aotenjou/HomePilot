package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.entity.Device;
import com.example.manager.entity.Mqttdata;
import com.example.manager.entity.UserCustomPermission;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.MqttdataMapper;
import com.example.manager.mapper.UserCustomPermissionMapper;
import com.example.manager.mqtt.Service.MqttSendmessageService;
import com.example.manager.service.DeviceInteractService;
import com.example.manager.service.RoomService;
import com.example.manager.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceInteractServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceInteractService {

    @Autowired
    private UserCustomPermissionMapper userCustomPermissionMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MqttSendmessageService mqttSendmessageService;

    @Autowired
    private MqttdataMapper MqttDataMapper;

    @Override
    public void sendCommand(String deviceId, String operation) {
        mqttSendmessageService.sendMessage(deviceId, operation);
    }

    @Override
    public boolean checkDeviceOperationPermission(Long DeviceId, Long OperationId, Long userId) {
        return userCustomPermissionMapper.checkUserOperationPermission(DeviceId, OperationId, userId);
    }

    @Override
    public boolean checkDeviceOnlineStatus(Long DeviceId) {
        LocalDateTime time = deviceMapper.selectLastActiveTime(DeviceId);
        LocalDateTime timeNow = LocalDateTime.now();
        return TimeUtils.isEqual(time, timeNow);
    }

    @Override
    public boolean checkDeviceActiveStatus(Long DeviceId) {
        return deviceMapper.selectActiveStatus(DeviceId).equals(Device.ActiveStatus.ACTIVE.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean moveDevice(Long deviceId, Long roomId) {
        if (deviceId == null || roomId == null) {
            throw new IllegalArgumentException("设备ID或房间ID不能为空");
        }

        Device device = deviceMapper.selectById(deviceId).get(0);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        Long homeId = device.getHomeId();
        if (!roomService.checkRoom(roomId, homeId)) {
            throw new RuntimeException("目标移动房间不存在");
        }

        device.setRoomId(roomId);

        return deviceMapper.updateById(device) > 0;
    }

    @Override
    public List<Mqttdata> getDeviceData(Long deviceId) {
        return MqttDataMapper.getDeviceData(deviceId);
    }
}
