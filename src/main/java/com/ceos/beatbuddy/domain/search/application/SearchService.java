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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
        String keyword = searchRequestDTO.getKeyword().get(0);

        Double score =0.0;
        try {
            // 검색을하면 해당검색어를 value에 저장하고, score를 1 준다
            score = redisTemplate.opsForZSet().incrementScore("ranking", keyword,1.0);

            // 만료 시간 설정 (현재 시간 + 24시간)
            long expireAt = Instant.now().getEpochSecond() + 86400;
            Double expireAtDouble = Double.valueOf(expireAt);

            // 만료 시간을 Sorted Set에 저장
            Boolean expire = redisTemplate.opsForZSet().add("expire", keyword, expireAtDouble);
            System.out.println(expire);
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

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void removeExpiredElements() {
        String rankingKey = "ranking";
        String expireKey = "expire";
        long currentTime = Instant.now().getEpochSecond();
        Double currentTimeDouble = Double.valueOf(currentTime);
        System.out.println("Remove expired elements.");
        Set<String> expiredWords = redisTemplate.opsForZSet().rangeByScore(expireKey, 0, currentTimeDouble);
        if (expiredWords != null) {
            for (String word : expiredWords) {
                redisTemplate.opsForZSet().remove(rankingKey, word);
                redisTemplate.opsForZSet().remove(expireKey, word);
            }
        }
    }

}
