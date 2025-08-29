package com.example.manager.DTO;

import com.example.manager.entity.Device;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FamilyProfile {
    String familyName;
    String hostName;
    List<ViewHomeUser> familyMembers;
    List<Device> familyDevices;

    public FamilyProfile(String familyName, String hostName, List<ViewHomeUser> familyMembers, List<Device> familyDevices) {
        this.familyName = familyName;
        this.hostName = hostName;
        this.familyMembers = familyMembers;
        this.familyDevices = familyDevices;
    }
}
