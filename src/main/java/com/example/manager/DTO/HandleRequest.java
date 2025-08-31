package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandleRequest {
    Long userId;
    Long requestId;
    Integer status;

    public HandleRequest(Long userId, Long requestId, Integer status) {
        this.userId = userId;
        this.requestId = requestId;
        this.status = status;
    }
}
