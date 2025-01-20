package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.domain.member.dto.error.MemberErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode {

    NICKNAME_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    LOGINID_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 로그인 ID입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST, "잘못된 회원정보입니다."),
    INVALID_PASSWORD_INFO(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    MEMBER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다"),
    REGION_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 지역입니다."),
    REGION_FIELD_EMPTY(HttpStatus.NOT_FOUND, "관심 지역이 설정되어 있지 않습니다."),
    UNAVAILABLE_REGION(HttpStatus.NOT_FOUND, "사용자가 관심지역으로 선택했던 지역 리스트에 해당되지 않는 지역입니다."),
    NICKNAME_OVER_LENGTH(HttpStatus.NOT_FOUND, "닉네임이 12자 초과입니다"),
    NICKNAME_SPACE_EXIST(HttpStatus.NOT_FOUND, "닉네임에 공백이 있습니다"),
    NICKNAME_SYMBOL_EXIST(HttpStatus.NOT_FOUND, "닉네임에 특수문자가 있습니다"),
    USERNAME_NOT_MATCH(HttpStatus.BAD_REQUEST, "인증한 성명과 유저의 이름이 일치하지 않습니다."),
    MEMBER_NOT_ADULT(HttpStatus.BAD_REQUEST, "인증한 유저는 성인이 아닙니다."),
    NOT_ADMIN(HttpStatus.BAD_REQUEST, "해당 계정은 어드민이 아닙니다.");

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

