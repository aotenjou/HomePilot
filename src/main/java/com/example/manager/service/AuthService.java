package com.example.manager.service;

import com.example.manager.entity.User;

public interface AuthService {
    boolean checkPhone(String phone);

    boolean checkPassword(String username, String password);

    void createUser(User user);

    String getToken(String userId);

    Long getUserIdByPhone(String phone);

    User getUserByPhone(String phone);
}

