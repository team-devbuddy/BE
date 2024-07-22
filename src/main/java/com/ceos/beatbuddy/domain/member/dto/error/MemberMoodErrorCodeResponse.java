package com.ceos.beatbuddy.domain.member.dto.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberMoodErrorCodeResponse {
    private HttpStatus status;
    private String message;

    public MemberMoodErrorCodeResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
