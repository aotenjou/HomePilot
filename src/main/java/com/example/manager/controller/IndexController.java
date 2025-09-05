package com.example.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "<html><body>" +
                "<h1>智能家居管理系统 API</h1>" +
                "<p>欢迎使用智能家居管理系统后端API服务</p>" +
                "<h3>主要API接口:</h3>" +
                "<ul>" +
                "<li><a href='/auth'>/auth</a> - 用户认证接口</li>" +
                "<li><a href='/home'>/home</a> - 家庭管理接口</li>" +
                "<li><a href='/guest'>/guest</a> - 访客权限接口</li>" +
                "<li><a href='/api/security'>/api/security</a> - 安全管理接口</li>" +
                "<li><a href='/test'>/test</a> - 测试接口</li>" +
                "</ul>" +
                "<p><strong>注意:</strong> 这是一个RESTful API服务，请使用适当的HTTP方法和请求体访问各个接口。</p>" +
                "</body></html>";
    }
}