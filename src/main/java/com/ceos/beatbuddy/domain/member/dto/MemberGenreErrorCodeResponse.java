package com.ceos.beatbuddy.domain.member.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberGenreErrorCodeResponse {
    private HttpStatus status;
    private String message;

    public MemberGenreErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
