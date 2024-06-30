package com.ceos.beatbuddy.domain.vector.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VectorErrorCodeResponse {
    private HttpStatus status;
    private String message;

    public VectorErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
