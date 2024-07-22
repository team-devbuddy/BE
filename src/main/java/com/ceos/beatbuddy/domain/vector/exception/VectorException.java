package com.ceos.beatbuddy.domain.vector.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class VectorException extends ResponseException {

    public VectorException(VectorErrorCode vectorErrorCode) {
        super(vectorErrorCode.getMessage(), vectorErrorCode.getHttpStatus());
    }

}
