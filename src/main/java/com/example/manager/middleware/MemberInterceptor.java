package com.example.manager.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberInterceptor implements HandlerInterceptor {
    @Autowired
    private RoleChecker roleChecker;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!roleChecker.checkMemberRole(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "非家庭成员没有权限进行此操作");
            return false;
        }
        return true;
    }
}
