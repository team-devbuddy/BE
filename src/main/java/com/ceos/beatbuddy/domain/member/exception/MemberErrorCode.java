package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.domain.member.dto.MemberErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode {

    ID_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 ID입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST, "잘못된 회원정보입니다."),
    INVALID_PASSWORD_INFO(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    MEMBER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다"),
    REGION_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 지역입니다.");

    private final HttpStatus httpStatus;
    private final String message;


    MemberErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public MemberErrorCodeResponse toResponse() {
        return new MemberErrorCodeResponse(this.httpStatus, this.message);
    }
}

