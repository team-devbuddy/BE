package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class MemberMoodException extends ResponseException {

    public MemberMoodException(MemberMoodErrorCode memberMoodErrorCode) {
        super(memberMoodErrorCode.getMessage(), memberMoodErrorCode.getHttpStatus());
    }
}