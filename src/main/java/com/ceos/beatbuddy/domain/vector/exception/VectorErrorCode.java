package com.ceos.beatbuddy.domain.vector.exception;

import com.ceos.beatbuddy.domain.vector.dto.VectorErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum VectorErrorCode {

    NOT_SAME_LENGTH(HttpStatus.BAD_REQUEST, "두 벡터의 길이가 다릅니다."),
    GENRE_INDEX_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 문자열의 장르는 리스트에 없습니다."),
    MOOD_INDEX_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 문자열의 분위기는 리스트에 없습니다."),
    VECTOR_ZERO_NORM(HttpStatus.BAD_REQUEST, "벡터의 norm이 0입니다."),
    TAGS_EMPTY(HttpStatus.BAD_REQUEST, "필터링할 키워드가 비어 있습니다."),
    UNAVAILABLE_GENRE(HttpStatus.NOT_FOUND, "사용자가 취향으로 선택했던 장르 태그에 해당하지 않는 장르입니다."),
    UNAVAILABLE_MOOD(HttpStatus.NOT_FOUND, "사용자가 취향으로 선택했던 무드 태그에 해당하지 않는 무드입니다."),
    UNAVAILABLE_INPUT(HttpStatus.BAD_REQUEST, "input값이 리스트에 존재하지 않습니다. DB에 input이 불가능한 값입니다.");
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
