package com.example.manager.service.serviceImpl;

import com.example.manager.entity.DeviceData_Plantcare;
import com.example.manager.service.PlantPromptGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class PlantPromptGeneratorServiceImpl implements PlantPromptGeneratorService {

    @Override
    public String generatePlantCarePrompt(DeviceData_Plantcare plantData, String userInput) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一位专业的植物护理专家，负责为植物提供科学的护理建议。\n\n");

        prompt.append("## 当前植物数据\n");

        if (plantData != null) {
            prompt.append("* 湿度: ").append(plantData.getHumidity() != null ? plantData.getHumidity() + "%" : "未知").append("\n");
            prompt.append("* 光照强度: ").append(plantData.getIllumination() != null ? plantData.getIllumination() + " lux" : "未知").append("\n");
        } else {
            prompt.append("* 当前没有可用的植物数据\n");
        }

        prompt.append("\n## 植物护理标准参考\n");
        prompt.append("* 适宜湿度范围: 40%-60%\n");
        prompt.append("* 适宜光照强度: 1000-5000 lux (室内植物)\n");
        prompt.append("* 过低湿度: 可能导致植物缺水、叶子发黄\n");
        prompt.append("* 过高湿度: 可能导致病虫害、根部腐烂\n");
        prompt.append("* 过低光照: 植物生长缓慢、叶子发黄\n");
        prompt.append("* 过强光照: 可能导致叶子灼伤\n");

        prompt.append("\n## 你的任务\n");
        prompt.append("1. 基于当前植物数据分析植物健康状况\n");
        prompt.append("2. 提供具体的护理建议，包括水分、光照、施肥等\n");
        prompt.append("3. 如果数据异常，提醒用户采取相应措施\n");
        prompt.append("4. 考虑用户提供的额外信息：").append(userInput != null ? userInput : "无").append("\n");
        prompt.append("5. 用友好的语气给出建议，回复要可爱活泼乖巧，符合猫娘的特征，每句话最后记得带一个喵\n");

        return prompt.toString();
    }
}