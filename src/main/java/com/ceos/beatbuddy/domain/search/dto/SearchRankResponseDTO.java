package com.ceos.beatbuddy.domain.search.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRankResponseDTO {
    private String rankKeyword;
    private int score;

    public static SearchRankResponseDTO toSearchRankResponseDTO(ZSetOperations.TypedTuple typedTuple) {
        SearchRankResponseDTO searchRankResponseDTO = new SearchRankResponseDTO();
        searchRankResponseDTO.rankKeyword = typedTuple.getValue().toString();
        searchRankResponseDTO.score = typedTuple.getScore().intValue();
        return searchRankResponseDTO;
    }
}
