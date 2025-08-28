package com.example.manager.service;

import com.example.manager.DTO.OperationOfDevice;
import com.example.manager.DTO.SceneDeviceView;
import com.example.manager.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface SceneService {
    List<Device> getHomeDevices(Long homeId);

    void createScene(Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime, List<OperationOfDevice> deviceOperationId);

    void createSceneWithoutDevice(Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime);

    boolean checkScene(Long id);

    void deleteScene(Long id);

    List<Scene> getHomeScene(Long homeId);

    List<DeviceInScene> getDeviceOperation(Long sceneId);

    void updateScene(Long sceneId, Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime, List<OperationOfDevice> deviceOperationId);

    void updateSceneWithoutDevice(Long sceneId, Long userId, Long homeId, String name, String description, Integer status, LocalDateTime startTime, LocalDateTime endTime);

    List<SceneDeviceView> getSceneDeviceView(Long sceneId);
}
