package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
    private String input;

    public ChatRequest(String input) {
        this.input = input;
    }
}
