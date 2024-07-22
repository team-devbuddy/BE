package com.ceos.beatbuddy.domain.search.dto;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class SearchDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDTO {
        private List<String> keyword;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class mediumDTO {
        private LocalDateTime currentDate;
        private Long venueId;
        private String englishName;
        private String koreanName;
        private Long heartbeatNum;
        private String venueGenre;
        private String venueMood;
        private Region region;
    }
}
