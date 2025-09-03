package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.DTO.OperationOfDevice;
import com.example.manager.DTO.SceneDeviceView;
import com.example.manager.entity.*;
import com.example.manager.mapper.DeviceInSceneMapper;
import com.example.manager.mapper.DeviceMapper;
import com.example.manager.mapper.SceneDeviceViewMapper;
import com.example.manager.mapper.SceneMapper;
import com.example.manager.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements SceneService {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private DeviceInSceneMapper deviceInSceneMapper;

    @Autowired
    private SceneDeviceViewMapper sceneDeviceViewMapper;

    @Override
    public List<Device> getHomeDevices(Long homeId) {
        return deviceMapper.selectByHomeId(homeId);
    }

    @Override
    public void createScene(Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime, List<OperationOfDevice> deviceOperation) {
        sceneMapper.insertScene(userId, homeId, name, description, status, startTime, endTime);

        Long sceneId = sceneMapper.selectLastInsertId();

        // 过滤无效设备操作，避免 NULL 插入导致约束异常
        List<OperationOfDevice> validOperations = deviceOperation == null ? List.of() :
                deviceOperation.stream()
                        .filter(op -> op != null && op.getDeviceId() != null && op.getOperationId() != null)
                        .toList();

        if (!validOperations.isEmpty()) {
            deviceInSceneMapper.createDeviceInScene(validOperations, sceneId);
        }
    }

    @Override
    public void createSceneWithoutDevice(Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        sceneMapper.insertScene(userId, homeId, name, description, status, startTime, endTime);
    }

    @Override
    public boolean checkScene(Long id) {
        Scene scene = sceneMapper.selectActiveById(id);
        return scene != null;
    }

    @Override
    public void deleteScene(Long id) {
        sceneMapper.deleteSceneById(id);
        deviceInSceneMapper.deleteDeviceInScene(id);
    }

    @Override
    public List<Scene> getHomeScene(Long homeId) {
        return sceneMapper.selectByHomeId(homeId);
    }

    @Override
    public List<DeviceInScene> getDeviceOperation(Long sceneId) {
        return deviceInSceneMapper.selectBySceneId(sceneId);
    }

    @Override
    public void updateScene(Long sceneId, Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime, List<OperationOfDevice> deviceOperation) {
        Scene scene = new Scene();
        scene.setId(sceneId);
        scene.setUserId(userId);
        scene.setHomeId(homeId);
        scene.setName(name);
        scene.setDescription(description);
        scene.setStatus(status);
        scene.setStartTime(startTime);
        scene.setEndTime(endTime);
        sceneMapper.updateScene(scene);

        // 过滤有效操作
        List<OperationOfDevice> validOperations = deviceOperation == null ? List.of() :
                deviceOperation.stream()
                        .filter(op -> op != null && op.getDeviceId() != null && op.getOperationId() != null)
                        .toList();

        deviceInSceneMapper.deleteDeviceNotInScene(sceneId, validOperations);
        if (!validOperations.isEmpty()) {
            deviceInSceneMapper.createDeviceInScene(validOperations, sceneId);
            deviceInSceneMapper.updateDeviceInScene(sceneId, validOperations);
        }
    }

    @Override
    public void updateSceneWithoutDevice(Long sceneId, Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        Scene scene = new Scene();
        scene.setId(sceneId);
        scene.setUserId(userId);
        scene.setHomeId(homeId);
        scene.setName(name);
        scene.setDescription(description);
        scene.setStatus(status);
        scene.setStartTime(startTime);
        scene.setEndTime(endTime);
        sceneMapper.updateScene(scene);

        deviceInSceneMapper.deleteDeviceInScene(sceneId);
    }

    @Override
    public List<SceneDeviceView> getSceneDeviceView(Long sceneId) {
        return sceneDeviceViewMapper.getSceneDeviceView(sceneId);
    }
}
