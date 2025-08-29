package com.example.manager.service;

import com.example.manager.DTO.FamilyProfile;

public interface PromptGeneratorService {
    String generatePrompt(Long homeId, Long userId);

    FamilyProfile getFamilyProfile(Long homeId, Long userId);
}
