package com.example.manager.middleware;

import com.example.manager.entity.UserHome;
import com.example.manager.mapper.UserHomeMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RoleChecker {
    private static final Logger logger = LoggerFactory.getLogger(RoleChecker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserHomeMapper userHomeMapper;

    public boolean checkHostRole(HttpServletRequest request) throws IOException {
        Object userParam = request.getAttribute("currentUserId");
        if(!(userParam instanceof Long userId)) {
            logger.warn("User ID not found in request attributes.");
            return false;
        }

        Long homeId;
        try {
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );

            if (pathVariables != null) {
                String homeIdStr = pathVariables.get("homeId"); // 按变量名获取
                if (homeIdStr != null) {
                    homeId = Long.parseLong(homeIdStr);
                } else {
                    logger.warn("homeId not found in path variables");
                    return false;
                }
            } else {
                logger.warn("Path variables not available");
                return false;
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid home ID format: {}", request.getRequestURI());
            return false;
        }
        
        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            logger.warn("User {} has no role in home {}.", userId, homeId);
            return false;
        }
        
        if (!roleCode.equals(UserHome.Role.HOST.getCode())) {
            logger.warn("User {} is not the host of home {}.", userId, homeId);
            return false;
        }

        return true;
    }

    public boolean checkMemberRole(HttpServletRequest request) {
        Object userParam = request.getAttribute("currentUserId");
        if(!(userParam instanceof Long userId)) {
            logger.warn("User ID not found in request attributes.");
            return false;
        }

        Long homeId;
        try {
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );

            if (pathVariables != null) {
                String homeIdStr = pathVariables.get("homeId"); // 按变量名获取
                if (homeIdStr != null) {
                    homeId = Long.parseLong(homeIdStr);
                } else {
                    logger.warn("homeId not found in path variables");
                    return false;
                }
            } else {
                logger.warn("Path variables not available");
                return false;
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid home ID format: {}", request.getRequestURI());
            return false;
        }
        
        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            logger.warn("User {} has no role in home {}.", userId, homeId);
            return false;
        }

        if (roleCode > UserHome.Role.MEMBER.getCode()) {
            logger.warn("User {} is not a member of home {}.", userId, homeId);
            return false;
        }

        return true;
    }

    public boolean checkRoleInHome(HttpServletRequest request) {
        Object userParam = request.getAttribute("currentUserId");
        if(!(userParam instanceof Long userId)) {
            logger.warn("User ID not found in request attributes.");
            return false;
        }

        Long homeId;
        try {
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );

            if (pathVariables != null) {
                String homeIdStr = pathVariables.get("homeId"); // 按变量名获取
                if (homeIdStr != null) {
                    homeId = Long.parseLong(homeIdStr);
                } else {
                    logger.warn("homeId not found in path variables");
                    return false;
                }
            } else {
                logger.warn("Path variables not available");
                return false;
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid home ID format: {}", request.getRequestURI());
            return false;
        }

        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);
        if (roleCode == null) {
            logger.warn("User {} has no role in home {}.", userId, homeId);
            return false;
        }

        request.setAttribute("roleCode", roleCode);
        request.setAttribute("homeId", homeId);

        return true;
    }
}