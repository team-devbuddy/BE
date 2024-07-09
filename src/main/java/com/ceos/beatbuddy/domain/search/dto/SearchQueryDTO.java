package com.ceos.beatbuddy.domain.search.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchQueryDTO {

    private Long venueId;
    private String englishName;
    private String koreanName;
    private String address;
    private String description;

    @QueryProjection
    public SearchQueryDTO(Long venueId, String englishName, String koreanName, String address, String description) {
        this.venueId = venueId;
        this.englishName = englishName;
        this.koreanName = koreanName;
        this.address = address;
        this.description = description;
    }


}
