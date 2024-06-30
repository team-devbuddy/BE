package com.ceos.beatbuddy.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class ResponseTemplate {
    public int status;

    public String message;

    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ResponseEntity<ResponseTemplate> toResponseEntity(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ResponseTemplate.builder()
                        .message(message)
                        .status(status.value())
                        .build());
    }
}