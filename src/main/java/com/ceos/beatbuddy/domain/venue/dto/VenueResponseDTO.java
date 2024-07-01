package com.ceos.beatbuddy.domain.venue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VenueResponseDTO {
    private Long venueId;
    private String englishName;
    private String koreanName;
}
