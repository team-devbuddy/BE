package com.ceos.beatbuddy.domain.member.exception;


import com.ceos.beatbuddy.domain.member.dto.MemberGenreErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberGenreErrorCode {

    MEMBER_GENRE_OVER_REQUEST(HttpStatus.BAD_REQUEST, "존재하는 선호 장르 벡터보다 요청 수가 많습니다"),
    MEMBER_GENRE_NOT_EXIST(HttpStatus.NOT_FOUND, "선호 장르가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;


    MemberGenreErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public MemberGenreErrorCodeResponse toResponse() {
        return new MemberGenreErrorCodeResponse(this.httpStatus, this.message);
    }
}
