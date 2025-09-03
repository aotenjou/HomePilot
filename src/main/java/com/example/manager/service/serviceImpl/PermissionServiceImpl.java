package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.entity.UserCustomPermission;
import com.example.manager.mapper.UserCustomPermissionMapper;
import com.example.manager.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionServiceImpl extends ServiceImpl<UserCustomPermissionMapper, UserCustomPermission> implements PermissionService{
    @Autowired
    private UserCustomPermissionMapper baseMapper;

    @Override
    public void createUserCustomPermission(UserCustomPermission userCustomPermission) {
        baseMapper.createUserCustomPermission(userCustomPermission);
    }

    @Override
    public Boolean checkPermission(Long id) {
        return baseMapper.checkPermission(id);
    }

    @Override
    public void cancelPermission(Long id) {
        baseMapper.deleteUserCustomPermission(id);
    }
}
