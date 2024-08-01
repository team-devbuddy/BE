package com.ceos.beatbuddy.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchMapDTO {
    private List<SearchQueryResponseDTO> venueList;
    private String genreTag;
    private String regionTag;
    private String sortCriteria;
}
