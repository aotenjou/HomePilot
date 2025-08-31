package com.example.manager.service;

import com.example.manager.DTO.ViewEnterRequest;
import com.example.manager.entity.EnterRequestRecord;

import java.util.List;

public interface EnterHomeRequestService {
    boolean checkHomeEnter(Long homeId, Long userId, Integer status);

    Long createEnterRequest(Long homeId, Long userId);

    boolean checkRecord(Long requestId);

    List<ViewEnterRequest> getAllEnterRequest(Long homeId);

    void handleEnterRequest(Long userId, Long homeId, Long requestId, Integer status);

    Integer checkRequestStatus(Long requestId);
}
