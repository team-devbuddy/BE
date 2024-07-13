package com.ceos.beatbuddy.domain.archive.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ArchiveRequestDTO {

    private Long memberGenreId;
    private Long memberMoodId;
    private Long memberId;
}
