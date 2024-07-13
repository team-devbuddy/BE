package com.ceos.beatbuddy.domain.archive.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ArchiveResponseDTO {

    private Long archiveId;
    private Long memberId;
    private String memberMoodVector;
    private String memberGenreVector;
    private LocalDateTime updatedAt;
}
