package com.example.manager.config;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    public String passwordEncoder(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
