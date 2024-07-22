package com.ceos.beatbuddy.domain.venue.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VenueGenreErrorCodeResponse {

    private final HttpStatus status;
    private final String message;

    public VenueGenreErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
