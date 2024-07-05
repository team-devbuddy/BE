package com.ceos.beatbuddy.domain.heartbeat.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HeartbeatErrorCodeResponse {

    private HttpStatus status;
    private String message;

    public HeartbeatErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
