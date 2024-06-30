package com.ceos.beatbuddy.domain.venue.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class VenueErrorCodeResponse {
    private HttpStatus status;
    private String message;

    public VenueErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
