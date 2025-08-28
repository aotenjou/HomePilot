package com.example.manager.controller;

import com.example.manager.DTO.ChatRequest;
import com.example.manager.service.PromptGeneratorService;
import com.example.manager.service.serviceImpl.PromptGeneratorServiceImpl;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import io.reactivex.Emitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Tag(name = "AI 聊天接口", description = "基于大模型的智能聊天接口，支持流式输出")
@Slf4j
@RestController
@RequestMapping("/home/{homeId}/ai")
public class LLMController {
    @Autowired
    PromptGeneratorService promptGeneratorService;

    @Value("${ark.api.key}")
    private String apiKey;

    @Value("${ark.api.model}")
    private String model;

    @Value("${ark.api.temperature}")
    private double temperature;

    @Value("${ark.api.max_tokens}")
    private int maxTokens;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final int MAX_INPUT_LENGTH = 2000;

    @Operation(
            summary = "AI 聊天流式接口",
            description = "使用大模型与用户进行智能聊天对话，返回 SSE（Server-Sent Events）数据流"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "聊天响应流式返回成功", content = @Content(mediaType = "text/event-stream")),
            @ApiResponse(responseCode = "500", description = "服务异常", content = @Content)
    })
    @PostMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatRequest request,
                           @Parameter(hidden = true)@RequestAttribute("currentUserId") Long userId,
                           @Parameter(description = "家庭ID", required = true)@PathVariable("homeId") Long homeId,
                           @RequestHeader HttpHeaders headers) {
        String prompt = promptGeneratorService.generatePrompt(homeId, userId);

        String truncatedInput = request.getInput().length() > MAX_INPUT_LENGTH
                ? request.getInput().substring(0, MAX_INPUT_LENGTH)
                : request.getInput();

        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        executor.execute(() -> {
            try {
                ArkService arkService = ArkService.builder().apiKey(apiKey).build();

                List<ChatMessage> streamMessages = new ArrayList<>();

                ChatMessage streamSystemMessage = ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(prompt)
                        .build();

                ChatMessage streamUserMessage = ChatMessage.builder()
                        .role(ChatMessageRole.USER)
                        .content(truncatedInput)
                        .build();
                streamMessages.add(streamSystemMessage);
                streamMessages.add(streamUserMessage);

                ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                        .model(model)
                        .messages(streamMessages)
                        .temperature(temperature)
                        .maxTokens(maxTokens)
                        .stream(true)
                        .build();

                arkService.streamChatCompletion(streamChatCompletionRequest)
                        .doOnError(throwable -> {
                            try {
                                emitter.completeWithError(throwable);
                            } catch (Exception e) {
                                log.error("Error occurred: {}", throwable.getMessage());
                            } finally {
                                emitter.completeWithError(throwable);
                            }
                        })
                        .blockingForEach(response -> {
                            if(response.getChoices() != null && !response.getChoices().isEmpty()) {
                                Object message = response.getChoices().get(0).getMessage().getContent();
                                if(message != null) {
                                    emitter.send(SseEmitter.event()
                                            .data(message)
                                            .id(String.valueOf(System.currentTimeMillis()))
                                    );
                                }
                            }
                        });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        emitter.onCompletion(() -> System.out.println("SSE 完成"));
        emitter.onTimeout(() -> System.out.println("SSE 超时"));
        emitter.onError(throwable -> System.out.println("SSE 错误: " + throwable.getMessage()));

        return emitter;
    }
}
