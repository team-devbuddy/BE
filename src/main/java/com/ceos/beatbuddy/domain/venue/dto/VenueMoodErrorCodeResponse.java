package com.ceos.beatbuddy.domain.venue.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VenueMoodErrorCodeResponse {

    private final HttpStatus status;
    private final String message;

    public VenueMoodErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
