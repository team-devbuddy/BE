package com.ceos.beatbuddy.domain.venue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VenueResponseDTO {
    private Long venueId;
    private String englishName;
    private String koreanName;
}
