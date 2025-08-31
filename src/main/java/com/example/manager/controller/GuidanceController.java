package com.example.manager.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/guidance")
public class GuidanceController {

    private static final String BAILIAN_API_URL = "https://bailian.aliyuncs.com/v1/chat/completions";
    private static final String API_KEY = "sk-bd5fd9bd84bf4d40a6adb7ad93d38bb5";

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(60_000L); // 设置超时时间为60秒

        executor.execute(() -> {
            try {
                callBailianApiWithStreaming(message, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private void callBailianApiWithStreaming(String message, SseEmitter emitter) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(BAILIAN_API_URL);

        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);

        // 构建请求体
        String requestBody = String.format("{\"model\":\"qwen-max\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"stream\":true}",
                message.replace("\"", "\\\""));

        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 直接发送API返回的字符串内容
                        if (!line.trim().isEmpty()) {
                            emitter.send(line);
                        }
                    }
                    emitter.complete();
                }
            } else {
                emitter.completeWithError(new RuntimeException("API请求失败: " + response.getStatusLine()));
            }
        }
    }
}