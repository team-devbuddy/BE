package com.ceos.beatbuddy.global.config.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OauthErrorCode {

    LOGOUT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;


    OauthErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}