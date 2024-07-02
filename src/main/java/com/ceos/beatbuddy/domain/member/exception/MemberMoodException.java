package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class MemberMoodException extends ResponseException {

    public MemberMoodException(MemberMoodErrorCode memberGenreErrorCode) {
        super(memberGenreErrorCode.getMessage(), memberGenreErrorCode.getHttpStatus());
    }
}