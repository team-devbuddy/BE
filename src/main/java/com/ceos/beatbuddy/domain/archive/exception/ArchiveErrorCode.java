package com.ceos.beatbuddy.domain.archive.exception;

import com.ceos.beatbuddy.domain.archive.dto.ArchiveErrorCodeResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArchiveErrorCode {


    ARCHIVE_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 해당 조합의 아카이브가 존재합니다."),
    ARCHIVE_NOT_EXIST(HttpStatus.NOT_FOUND, "선호 장르가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;


    ArchiveErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ArchiveErrorCodeResponse toResponse() {
        return new ArchiveErrorCodeResponse(this.httpStatus, this.message);
    }
}
