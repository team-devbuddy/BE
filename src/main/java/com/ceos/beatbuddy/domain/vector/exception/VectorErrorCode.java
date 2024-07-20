package com.ceos.beatbuddy.domain.vector.exception;

import com.ceos.beatbuddy.domain.vector.dto.VectorErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum VectorErrorCode {

    NOT_SAME_LENGTH(HttpStatus.BAD_REQUEST, "두 벡터의 길이가 다릅니다."),
    INDEX_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 문자열의 장르는 리스트에 없습니다."),
    VECTOR_ZERO_NORM(HttpStatus.BAD_REQUEST, "벡터의 norm이 0입니다");

    private final HttpStatus httpStatus;
    private final String message;


    VectorErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public VectorErrorCodeResponse toResponse() {
        return new VectorErrorCodeResponse(this.httpStatus, this.message);
    }
}
