package com.example.manager.middleware;

import com.example.manager.entity.RoleDefaultPermission;
import com.example.manager.entity.UserCustomPermission;
import com.example.manager.mapper.RoleDefaultPermissionMapper;
import com.example.manager.mapper.UserCustomPermissionMapper;
import com.example.manager.mapper.UserHomeMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class DevicePermissionChecker {
    private static final Logger logger = LoggerFactory.getLogger(DevicePermissionChecker.class);

    @Autowired
    private UserCustomPermissionMapper userCustomPermissionMapper;

    @Autowired
    private RoleDefaultPermissionMapper roleDefaultPermissionMapper;

    @Autowired
    private UserHomeMapper userHomeMapper;

    public boolean checkPermission(HttpServletRequest request) {
        Object userParam = request.getAttribute("currentUserId");
        if(!(userParam instanceof Long userId)) {
            logger.warn("User ID not found in request attributes.");
            return false;
        }

        Long deviceId, operationId, homeId;
        try {
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );

            if (pathVariables != null) {
                String deviceIdStr = pathVariables.get("deviceId");
                String operationIdStr = pathVariables.get("operationId");
                String homeIdStr = pathVariables.get("homeId");
                if (deviceIdStr != null && operationIdStr != null) {
                    deviceId = Long.valueOf(deviceIdStr);
                    operationId = Long.valueOf(operationIdStr);
                    homeId = Long.valueOf(homeIdStr);
                } else {
                    logger.warn("deviceId and operationId not found in path variables");
                    return false;
                }
            } else {
                logger.warn("Path variables not available");
                return false;
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid ID format: {}", request.getRequestURI());
            return false;
        }

        UserCustomPermission permission = userCustomPermissionMapper.selectPermission(userId, deviceId, operationId);

        if(permission != null && permission.getHasPermission().equals(false)) {
            logger.warn("User {} does not have permission to access device {}.", userId, deviceId);
            return false;
        } else if(permission != null && permission.getHasPermission().equals(true)) {
            return true;
        }

        Integer roleCode = userHomeMapper.selectRoleByUserIdAndHomeId(userId, homeId);

        if(roleDefaultPermissionMapper.selectPermission(roleCode, deviceId, operationId)==null) {
            logger.warn("User {} does not have permission to access device {}.", userId, deviceId);
            return false;
        }

        return true;
    }
}
