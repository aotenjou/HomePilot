package com.example.manager.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Environment {
    private LocalDateTime time;
    private String homeLocation;

    public Environment(LocalDateTime time, String homeLocation) {
        this.time = time;
        this.homeLocation = homeLocation;
    }
}
