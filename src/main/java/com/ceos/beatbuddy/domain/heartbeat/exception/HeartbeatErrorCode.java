package com.ceos.beatbuddy.domain.heartbeat.exception;

import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HeartbeatErrorCode {

    HEARTBEAT_NOT_EXIST(HttpStatus.NOT_FOUND, "해당되는 Heartbeat가 없습니다");

    private final HttpStatus httpStatus;
    private final String message;


    HeartbeatErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HeartbeatErrorCodeResponse toResponse() {
        return new HeartbeatErrorCodeResponse(this.httpStatus, this.message);
    }
}
