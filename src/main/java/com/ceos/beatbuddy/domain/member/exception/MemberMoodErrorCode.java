package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.domain.member.dto.MemberGenreErrorCodeResponse;
import com.ceos.beatbuddy.domain.member.dto.MemberMoodErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberMoodErrorCode {
    MEMBER_MOOD_OVER_REQUEST(HttpStatus.BAD_REQUEST, "존재하는 선호 분위기 벡터보다 요청 수가 많습니다"),
    MEMBER_MOOD_NOT_EXIST(HttpStatus.NOT_FOUND, "선호 분위기가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;


    MemberMoodErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public MemberMoodErrorCodeResponse toResponse() {
        return new MemberMoodErrorCodeResponse(this.httpStatus, this.message);
    }
}
