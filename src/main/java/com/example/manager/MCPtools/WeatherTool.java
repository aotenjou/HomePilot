package com.example.manager.MCPtools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Component
public class WeatherTool {
    @Tool(name = "天气查询", description = "查询指定城市的天气信息")
    Map getCurrentWeather(final String city) {
        RestClient client = RestClient.create(URI.create("https://api.vvhan.com"));
        Map<?, ?> result = client.get().uri("/api/weather?city={0}"+ city).retrieve().body(Map.class);
        return result;
    }
}