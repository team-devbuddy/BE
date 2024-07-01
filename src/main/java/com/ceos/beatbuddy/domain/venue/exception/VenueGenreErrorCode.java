package com.ceos.beatbuddy.domain.venue.exception;

import com.ceos.beatbuddy.domain.venue.dto.VenueGenreErrorCodeResponse;
import org.springframework.http.HttpStatus;

public enum VenueGenreErrorCode {

    INVALID_VENUE_GENRE_INFO(HttpStatus.BAD_REQUEST, "잘못된 베뉴 장르 정보입니다."),
    VENUE_GENRE_NOT_EXIST(HttpStatus.NOT_FOUND, "베뉴 장르가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;


    VenueGenreErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public VenueGenreErrorCodeResponse toResponse() {
        return new VenueGenreErrorCodeResponse(this.httpStatus, this.message);
    }
}
