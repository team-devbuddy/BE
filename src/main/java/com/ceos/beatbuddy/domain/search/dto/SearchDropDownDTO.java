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
public class SearchDropDownDTO {
    private List<String> keyword;
    private String genreTag;
    private String regionTag;
    private String sortCriteria;
}
