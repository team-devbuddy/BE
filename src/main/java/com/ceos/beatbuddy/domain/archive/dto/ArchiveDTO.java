package com.ceos.beatbuddy.domain.archive.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class ArchiveDTO {

    private Long archiveId;
    private Long memberId;
    private List<String> memberGenreList;
    private List<String> memberMoodList;
    private List<Region> regions;
    private LocalDateTime updatedAt;
}
