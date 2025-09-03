package com.example.manager.service;

import com.example.manager.entity.UserCustomPermission;

public interface PermissionService{
    void createUserCustomPermission(UserCustomPermission userCustomPermission);

    Boolean checkPermission(Long id);

    void cancelPermission(Long id);
}
