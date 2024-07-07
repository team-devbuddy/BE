package com.ceos.beatbuddy.domain.search.application;


import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchRankResponseDTO;
import com.ceos.beatbuddy.domain.search.repository.SearchRepository;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 검색을 했을 때 해당 글목록 리스트를 출력하는 메서드
    @Transactional
    public Page<SearchQueryDTO> keywordSearch(SearchDTO.RequestDTO searchRequestDTO, Pageable pageable) {

        Page<SearchQueryDTO> venueList= searchRepository.keywordFilter(searchRequestDTO, pageable);

        try {
            // 검색을하면 해당검색어를 value에 저장하고, score를 1 준다
            redisTemplate.opsForZSet().incrementScore("ranking", searchRequestDTO.getKeyword().get(0),1);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return venueList;
    }

    // 인기검색어 리스트 1위~10위까지
    public List<SearchRankResponseDTO> searchRankList() {
        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);  //score순으로 10개 보여줌
        return typedTuples.stream().map(SearchRankResponseDTO::toSearchRankResponseDTO).collect(Collectors.toList());
    }

}
