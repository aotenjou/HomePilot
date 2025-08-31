package com.example.manager.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenParser tokenParser;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!tokenParser.parse(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
            return false;
        }
        return true;
    }
}
