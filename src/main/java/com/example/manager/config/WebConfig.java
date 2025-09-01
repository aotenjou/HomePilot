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
        registry.addInterceptor(authInterceptor).addPathPatterns("/permission/**").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/permission/**").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/delete/*").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/delete/*").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/scene/**").order(1);
        registry.addInterceptor(memberInterceptor).addPathPatterns("/home/scene/**").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/create").order(1);

        registry.addInterceptor(authInterceptor).addPathPatterns("/room/create").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/room/create").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/room/delete").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/room/delete").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/get").order(1);

//        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(1);
//        registry.addInterceptor(roleInHomeInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(2);
//        registry.addInterceptor(deviceOperationInterceptor).addPathPatterns("/home/{deviceId}/operation/{operationId}").order(3);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/myHome").order(1);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/view/{homeId}").order(1);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/delete/{homeId}").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/delete/{homeId}").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/room/delete").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/room/delete").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/room/create").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/room/create").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/ai/chat").order(1);
        registry.addInterceptor(roleInHomeInterceptor).addPathPatterns("/home/{homeId}/ai/chat").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/updateName").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/updateName").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/updateAddress").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/updateAddress").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/scene/**").order(1);
        registry.addInterceptor(roleInHomeInterceptor).addPathPatterns("/home/{homeId}/scene/start/{sceneId}").order(2);
        registry.addInterceptor(memberInterceptor).addPathPatterns("/home/{homeId}/scene/add").order(2);
        registry.addInterceptor(memberInterceptor).addPathPatterns("/home/{homeId}/scene/delete/{sceneId}").order(2);
        registry.addInterceptor(memberInterceptor).addPathPatterns("/home/{homeId}/scene/stop/").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/request/**").order(1);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/request/receive").order(2);
        registry.addInterceptor(hostInterceptor).addPathPatterns("/home/{homeId}/request/receive/*").order(2);

        registry.addInterceptor(authInterceptor).addPathPatterns("/home/{homeId}/device/{deviceId}/operation/*").order(1);
        registry.addInterceptor(deviceOperationInterceptor).addPathPatterns("/home/{homeId}/device/{deviceId}/operation/*").order(2);
    }
}
