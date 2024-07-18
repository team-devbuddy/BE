package com.ceos.beatbuddy.domain.archive.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ArchiveUpdateDTO {
    private String memberMoodVector;
    private String memberGenreVector;
    private String regions;
}
