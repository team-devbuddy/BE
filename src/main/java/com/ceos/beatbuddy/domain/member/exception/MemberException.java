package com.ceos.beatbuddy.domain.member.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class MemberException extends ResponseException {

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage(), memberErrorCode.getHttpStatus());
    }
}