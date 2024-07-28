package com.ceos.beatbuddy.domain.search.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchQueryResponseDTO {

    private LocalDateTime currentDate;
    private Long venueId;
    private String englishName;
    private String koreanName;
    private List<String> tagList;
    private Long heartbeatNum;
    private Boolean isHeartbeat;
    private String logoUrl;
    private List<String> backgroundUrl;
    private String address;

    @QueryProjection
    public SearchQueryResponseDTO(LocalDateTime currentDate, Long venueId, String englishName, String koreanName, List<String> tagList, Long heartbeatNum, boolean isHeartbeat, String logoUrl, List<String> backgroundUrl, String address) {
        this.currentDate = currentDate;
        this.venueId = venueId;
        this.englishName = englishName;
        this.koreanName = koreanName;
        this.tagList = tagList;
        this.heartbeatNum = heartbeatNum;
        this.isHeartbeat = isHeartbeat;
        this.logoUrl = logoUrl;
        this.backgroundUrl = backgroundUrl;
        this.address = address;
    }


}
