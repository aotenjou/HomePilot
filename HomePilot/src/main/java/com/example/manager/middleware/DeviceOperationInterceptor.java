package com.example.manager.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DeviceOperationInterceptor implements HandlerInterceptor {
    @Autowired
    private DevicePermissionChecker devicePermissionChecker;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!devicePermissionChecker.checkPermission(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限进行此操作");
            return false;
        }
        return true;
    }
}
