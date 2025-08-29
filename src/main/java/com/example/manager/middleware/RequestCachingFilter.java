package com.example.manager.middleware;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest requestToUser = request;
        if(!(request instanceof ContentCachingRequestWrapper)) {
            requestToUser = new ContentCachingRequestWrapper(request);
        }
        filterChain.doFilter(requestToUser, response);
    }
}
