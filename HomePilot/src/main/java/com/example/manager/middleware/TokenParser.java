package com.example.manager.middleware;

import com.example.manager.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenParser {
    private static final Logger logger = LoggerFactory.getLogger(TokenParser.class);

    @Autowired
    private JWTUtils jwtUtils;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public boolean parse(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
            try {
                Claims claims = jwtUtils.parseToken(token);

                Date expiration = claims.getExpiration();
                if (expiration != null && expiration.before(new Date())) {
                    logger.warn("Token expired: {}", token);
                    return false;
                }

                String userIdStr = claims.getSubject();
                if (userIdStr == null || userIdStr.isEmpty()) {
                    logger.warn("User ID is empty in token");
                    return false;
                }
                Long userId = Long.valueOf(userIdStr);

                request.setAttribute("currentUserId", userId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to parse token: {}", token, e);
                return false;
            }
        }
        return false;
    }
}