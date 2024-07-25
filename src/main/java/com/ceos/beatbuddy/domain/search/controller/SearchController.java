package com.ceos.beatbuddy.domain.search.controller;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.search.application.SearchService;
import com.ceos.beatbuddy.domain.search.dto.*;
import com.ceos.beatbuddy.global.ResponseTemplate;
import com.ceos.beatbuddy.global.config.jwt.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Tag(name = "Search Controller", description = "검색 컨트롤러\n"
        + "사용자가 검색바에 검색하는 기능, 실시간 검색어 차트 조회 기능이 있습니다.")
public class SearchController {
    private final RedisTemplate<String, String> redisTemplate;
    private final SearchService searchService;

    @PostMapping("")
    @Operation(summary = "검색바 검색 기능", description = "사용자가 검색바에 입력한 검색어를 기반으로 베뉴 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색어로 베뉴 조회 성공"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = SearchResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "검색어가 입력되지 않아서 검색 실패"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ResponseTemplate.class)))
    })
    public ResponseEntity<List<SearchQueryResponseDTO>> searchList(@RequestBody SearchDTO.RequestDTO searchRequestDTO) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return ResponseEntity.ok(searchService.keywordSearch(searchRequestDTO, memberId));
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


}
