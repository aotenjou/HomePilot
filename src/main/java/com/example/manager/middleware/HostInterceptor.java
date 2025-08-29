package com.example.manager.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class HostInterceptor implements HandlerInterceptor {
    @Autowired
    private RoleChecker roleChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if(!roleChecker.checkHostRole(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "非房主没有权限进行此操作");
            return false;
        }
        return true;
    }
}
