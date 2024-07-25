package com.ceos.beatbuddy.domain.venue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VenueResponseDTO {
    private Long venueId;
    private String englishName;
    private String koreanName;
    private List<String> tagList;
    private Long heartbeatNum;
    private String logoUrl;
    private Boolean isHeartbeat;
}
