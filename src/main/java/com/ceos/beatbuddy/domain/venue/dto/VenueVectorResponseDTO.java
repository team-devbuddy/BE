package com.ceos.beatbuddy.domain.venue.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VenueVectorResponseDTO {
    private Long venueId;
    private Long vectorId;
    private String englishName;
    private String koreanName;
    private Region region;
    private String vectorString;
}
