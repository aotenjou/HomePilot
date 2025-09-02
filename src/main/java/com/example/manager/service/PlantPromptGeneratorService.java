package com.example.manager.service;

import com.example.manager.entity.DeviceData_Plantcare;

public interface PlantPromptGeneratorService {
    /**
     * 生成植物护理建议的prompt
     * @param plantData 植物数据
     * @param userInput 用户输入
     * @return 生成的prompt字符串
     */
    String generatePlantCarePrompt(DeviceData_Plantcare plantData, String userInput);
}
