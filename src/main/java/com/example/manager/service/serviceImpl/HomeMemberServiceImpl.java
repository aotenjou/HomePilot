package com.example.manager.service.serviceImpl;

import com.example.manager.mapper.UserHomeMapper;
import com.example.manager.service.HomeMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HomeMemberServiceImpl implements HomeMemberService {

    @Autowired
    private UserHomeMapper userHomeMapper;

    @Override
    public boolean isUserInHome(Long userId, Long homeId) {
        return userHomeMapper.countByUserIdAndHomeId(userId, homeId) > 0;
    }

    @Override
    public void addMemberToHome(Long homeId, Long userId, Integer role) {
        userHomeMapper.insertUserHome(userId, homeId, role);
    }
}
