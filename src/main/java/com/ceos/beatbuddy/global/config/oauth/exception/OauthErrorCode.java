package com.ceos.beatbuddy.global.config.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OauthErrorCode {

    LOGOUT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃에 실패했습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"잘못된 토큰 타입입니다.");

    private final HttpStatus httpStatus;
    private final String message;


    OauthErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}