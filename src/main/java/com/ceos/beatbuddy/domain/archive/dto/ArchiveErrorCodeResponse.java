package com.ceos.beatbuddy.domain.archive.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ArchiveErrorCodeResponse {
    private HttpStatus status;
    private String message;

    public ArchiveErrorCodeResponse(org.springframework.http.HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
