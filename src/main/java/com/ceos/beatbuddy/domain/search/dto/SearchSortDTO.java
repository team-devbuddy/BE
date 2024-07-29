package com.ceos.beatbuddy.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchSortDTO {
    private List<String> keyword;
    private String criteria;
}
