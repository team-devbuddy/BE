package com.ceos.beatbuddy.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchResultDTO<T> {
    private long totalElements;
    private int totalPages;
    private int size;
    private List<T> content;

    public SearchResultDTO(long totalElements, int totalPages, int size, List<T> content) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.content = content;
    }
}
