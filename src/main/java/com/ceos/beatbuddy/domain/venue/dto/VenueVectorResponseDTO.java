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
    Long venueId;
    Long vectorId;
    String englishName;
    String koreanName;
    Region region;
    String vectorString;
}
