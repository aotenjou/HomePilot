package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.DTO.SceneDeviceView;

import java.util.List;

public interface SceneDeviceViewMapper extends BaseMapper<SceneDeviceView> {
    List<SceneDeviceView> getSceneDeviceView(Long sceneId);
}
