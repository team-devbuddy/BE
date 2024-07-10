package com.ceos.beatbuddy.domain.search.controller;

import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.search.application.SearchService;
import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchRankResponseDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchResultDTO;
import com.ceos.beatbuddy.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "검색바 검색 기능", description = "사용자가 검색바에 입력한 검색어를 기반으로 베뉴 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색어로 베뉴 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = SearchResultDTO.class)))
    })
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
    @Operation(summary = "검색어 TOP10 차트 기능", description = "실시간 검색량 내림차순 검색어 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색어 TOP10 차트 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = SearchRankResponseDTO.class)))),
    })
    public List<SearchRankResponseDTO> searchRankList(){
        return searchService.searchRankList();
    }

}
