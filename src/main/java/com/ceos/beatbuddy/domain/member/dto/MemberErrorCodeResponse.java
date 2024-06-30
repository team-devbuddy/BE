package com.ceos.beatbuddy.domain.member.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberErrorCodeResponse {

    private HttpStatus status;
    private String message;

    public MemberErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
