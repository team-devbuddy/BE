package com.ceos.beatbuddy.domain.search.controller;

import com.ceos.beatbuddy.domain.search.application.SearchService;
import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchRankResponseDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchResultDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final RedisTemplate<String, String> redisTemplate;
    private final SearchService searchService;

    @GetMapping("")
    public SearchResultDTO<SearchQueryDTO> searchList(@RequestBody SearchDTO.RequestDTO searchRequestDTO,
                                                      @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<SearchQueryDTO> pageResult = searchService.keywordSearch(searchRequestDTO, pageable);
        SearchResultDTO<SearchQueryDTO> result = new SearchResultDTO<>(
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getSize(),
                pageResult.getContent()
        );

        return result;
    }

    @GetMapping("/rank")
    public List<SearchRankResponseDTO> searchRankList(){
        return searchService.searchRankList();
    }

}
