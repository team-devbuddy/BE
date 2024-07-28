package com.ceos.beatbuddy.domain.venue.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendFilterDTO {
    private List<String> regionTags;
    private List<String> moodTags;
    private List<String> genreTags;
}
