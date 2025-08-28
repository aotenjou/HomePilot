package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.DTO.ViewEnterRequest;
import com.example.manager.entity.EnterRequestRecord;
import com.example.manager.entity.GuestRecord;
import com.example.manager.entity.UserHome;
import com.example.manager.mapper.EnterRequestRecordMapper;
import com.example.manager.mapper.GuestRecordMapper;
import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.EnterHomeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EnterHomeRequestServiceImpl extends ServiceImpl<EnterRequestRecordMapper, EnterRequestRecord> implements EnterHomeRequestService {
    @Autowired
    EnterRequestRecordMapper enterRequestRecordMapper;

    @Autowired
    GuestRecordMapper guestRecordMapper;

    @Autowired
    UserHomeMapper userHomeMapper;

    @Override
    public boolean checkHomeEnter(Long homeId, Long userId, Integer status) {
        EnterRequestRecord record = enterRequestRecordMapper.selectByUserIdAndHomeId(userId, homeId, status);
        return record != null && record.getStatus().equals(EnterRequestRecord.Status.WAITING.getCode());
    }

    @Override
    public Long createEnterRequest(Long homeId, Long userId) {
        EnterRequestRecord record = new EnterRequestRecord();
        record.setHomeId(homeId);
        record.setUserId(userId);
        record.setStatus(EnterRequestRecord.Status.WAITING.getCode());
        record.setRecordTime(LocalDateTime.now());
        enterRequestRecordMapper.insert(record);
        return enterRequestRecordMapper.getLastInsertId();
    }

    @Override
    public boolean checkRecord(Long requestId) {
        return enterRequestRecordMapper.selectById(requestId) != null;
    }

    @Override
    public List<ViewEnterRequest> getAllEnterRequest(Long homeId) {
        return enterRequestRecordMapper.selectAllByHomeId(homeId);
    }

    @Override
    public void handleEnterRequest(Long userId, Long homeId, Long requestId, Integer status) {
        enterRequestRecordMapper.updateStatus(requestId, status);
        if (status.equals(EnterRequestRecord.Status.APPROVED.getCode())) {
            GuestRecord record = new GuestRecord();
            record.setHomeId(homeId);
            record.setUserId(userId);
            record.setRecordTime(LocalDateTime.now());
            record.setRecordType(GuestRecord.RecordType.ENTER.getCode());
            guestRecordMapper.insertRecord(record);
            userHomeMapper.createUserHome(userId, homeId, UserHome.Role.GUEST.getCode());
        }
    }

    @Override
    public Integer checkRequestStatus(Long requestId) {
        return enterRequestRecordMapper.selectStatus(requestId);
    }
}
