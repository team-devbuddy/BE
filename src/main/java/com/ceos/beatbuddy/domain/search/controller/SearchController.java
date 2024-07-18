package com.ceos.beatbuddy.domain.search.controller;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.search.application.SearchService;
import com.ceos.beatbuddy.domain.search.dto.*;
import com.ceos.beatbuddy.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "Search Controller", description = "검색 컨트롤러\n"
        + "사용자가 검색바에 검색하는 기능, 실시간 검색어 차트 조회 기능이 있습니다.")
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
                    , array = @ArraySchema(schema = @Schema(implementation = SearchRankResponseDTO.class))))
    })
    public List<SearchRankResponseDTO> searchRankList(){
        return searchService.searchRankList();
    }

    @GetMapping("/genre")
    @Operation(summary = "장르 필터링 검색 기능", description = "장르를 입력하면 해당 장르를 포함하는 베뉴 리스트를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 장르 베뉴 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = SearchRankResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "장르 리스트에 존재하지 않는 장르입니다"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = ResponseTemplate.class))))
    })
    public ResponseEntity<List<SearchQueryDTO>> searchByGenre(@RequestBody SearchGenreDTO searchGenreDTO){
        return ResponseEntity.ok(searchService.searchByGenre(searchGenreDTO.getGenre()));
    }

    @GetMapping("/region")
    @Operation(summary = "지역 필터링 검색 기능", description = "지역 4개 중 1개 선택하여 입력하면 그 지역의 베뉴 리스트를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 지역 베뉴 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , array = @ArraySchema(schema = @Schema(implementation = SearchRankResponseDTO.class))))
    })
    public ResponseEntity<List<SearchQueryDTO>> searchByRegion(@RequestBody String regionString){
        Region region = Region.fromText(regionString);
        return ResponseEntity.ok(searchService.searchByRegion(region));
    }

}
