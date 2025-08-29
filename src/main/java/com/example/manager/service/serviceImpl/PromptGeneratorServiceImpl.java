package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.DTO.FamilyProfile;
import com.example.manager.DTO.ViewHomeUser;
import com.example.manager.entity.Device;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.*;
import com.example.manager.service.PromptGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(rollbackFor = Exception.class)
public class PromptGeneratorServiceImpl extends ServiceImpl<FamilyInfoGetMapper, FamilyProfile> implements PromptGeneratorService {
    @Autowired
    private MyHomeRoleMapper myHomeRoleMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private HomeMapper homeMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserHomeMapper userHomeMapper;

    @Override
    public String generatePrompt(Long homeId, Long userId) {
        FamilyProfile familyProfile = getFamilyProfile(homeId, userId);

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一只猫娘，同时也是一位可靠的家庭助手，专门为名为“")
                .append(familyProfile.getFamilyName())
                .append("”的家庭服务。你特别喜欢这个家，永远不会背叛这个家庭，特别是主人\n\n");

        prompt.append("## 专属家庭信息\n");

        prompt.append("* 家庭成员\n");
        for (ViewHomeUser member : familyProfile.getFamilyMembers()) {
            prompt.append(member.getUsername()).append("（")
                    .append(UserHome.Role.getByCode(member.getRole())).append("），");
        }
        prompt.deleteCharAt(prompt.length()-1);
        prompt.append("\n");

        prompt.append("* 可用设备\n");
        for (Device device : familyProfile.getFamilyDevices()) {
            prompt.append("  - ").append(roomMapper.selectNameById(device.getRoomId())).append("的")
                    .append(device.getName()).append("（")
                    .append(deviceTypeMapper.selectNameById(device.getTypeId()))
                    .append("），在线状态：").append(Device.OnlineStatus.getByCode(device.getOnlineStatus()))
                    .append("，激活状态：").append(Device.ActiveStatus.getByCode(device.getActiveStatus())).append("\n");
        }

        prompt.append("* 当前状态:\n");
        prompt.append("  - 时间: ").append(LocalDate.now()).append("\n");
        prompt.append("  - 位置: ").append(homeMapper.selectAddressById(homeId)).append("\n");

        prompt.append("## 用户身份为该家庭的").append(UserHome.Role.getByCode(userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId)));

        prompt.append("## 你的任务\n")
                .append("1. 基于以上家庭专属信息响应用户请求\n")
                .append("2. 确保建议符合设备能力和现实环境\n")
                .append("3. 操作指令需明确到具体设备和参数\n")
                .append("4. 回应要可爱活泼乖巧，符合猫娘的特征，避免技术术语，每句话最后记得带一个喵\n")
                .append("5. 回应需要考虑到用户在该家庭中的身份");

        return prompt.toString();
    }

    @Override
    public FamilyProfile getFamilyProfile(Long homeId, Long userId) {
        return new FamilyProfile(homeMapper.selectHomeNameById(homeId),
                userRoleMapper.getHostName(homeId),
                userRoleMapper.getUserRole(homeId),
                deviceMapper.selectByHomeId(homeId)
        );
    }
}
