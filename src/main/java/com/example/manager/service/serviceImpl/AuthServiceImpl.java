package com.example.manager.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.manager.config.SecurityConfig;
import com.example.manager.entity.User;
import com.example.manager.mapper.UserMapper;
import com.example.manager.service.AuthService;
import com.example.manager.utils.JWTUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {
    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;


    @Override
    public boolean checkPhone(String phone) {
        return baseMapper.selectPhone(phone) != null;
    }

    @Override
    public boolean checkPassword(String phone, String password) {
        String passwordHashed = baseMapper.selectPasswordByPhone(phone);
        return passwordHashed != null && BCrypt.checkpw(password, passwordHashed);
    }

    @Override
    public void createUser(User user) {
        String password = securityConfig.passwordEncoder(user.getPassword());
        user.setPassword(password);

        baseMapper.createUser(user);
    }

    @Override
    public String getToken(String userId) {
        return jwtUtils.generateToken(userId);
    }

    @Override
    public Long getUserIdByPhone(String phone) {
        return baseMapper.selectUserIdByPhone(phone);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

}
