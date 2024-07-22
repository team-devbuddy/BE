package com.ceos.beatbuddy.domain.search.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SearchErrorCode {

    KEYWORD_IS_EMPTY(HttpStatus.BAD_REQUEST,"검색어가 입력되지 않았습니다." );

    private final HttpStatus httpStatus;
    private final String message;


    SearchErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
