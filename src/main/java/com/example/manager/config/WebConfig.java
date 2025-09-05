package com.example.manager.config;

import com.example.manager.middleware.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private HostInterceptor hostInterceptor;

    @Autowired
    private MemberInterceptor memberInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private RoleInHomeInterceptor roleInHomeInterceptor;

    @Autowired
    private DeviceOperationInterceptor deviceOperationInterceptor;

    @Bean
    public FilterRegistrationBean<RequestCachingFilter> requestCachingFilter() {
        FilterRegistrationBean<RequestCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestCachingFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("requestCachingFilter");
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
//        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(1);
//        registry.addInterceptor(roleInHomeInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(2);
//        registry.addInterceptor(deviceOperationInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(3);

       
    }
}
