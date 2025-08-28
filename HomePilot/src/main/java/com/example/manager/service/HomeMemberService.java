package com.example.manager.service;

public interface HomeMemberService {
    boolean isUserInHome(Long userId, Long homeId);

    void addMemberToHome(Long homeId, Long userId, Integer role);

}
