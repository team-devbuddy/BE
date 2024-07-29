package com.ceos.beatbuddy.domain.search.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SearchErrorCode {

    KEYWORD_IS_EMPTY(HttpStatus.BAD_REQUEST,"검색어가 입력되지 않았습니다." ),
    SORT_CRITERIA_EMPTY(HttpStatus.BAD_REQUEST,"정렬 기준이 입력되지 않았습니다." ),
    UNAVAILABLE_SORT_CRITERIA(HttpStatus.BAD_REQUEST,"'관련도순' 또는 '인기순'만 입력해주세요." );

    private final HttpStatus httpStatus;
    private final String message;


    SearchErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
