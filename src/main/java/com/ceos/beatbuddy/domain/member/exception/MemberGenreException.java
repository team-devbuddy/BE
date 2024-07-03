package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class MemberGenreException extends ResponseException {

    public MemberGenreException(MemberGenreErrorCode memberGenreErrorCode) {
        super(memberGenreErrorCode.getMessage(), memberGenreErrorCode.getHttpStatus());
    }
}