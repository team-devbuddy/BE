package com.ceos.beatbuddy.domain.search.application;


import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchDropDownDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryResponseDTO;
import com.ceos.beatbuddy.domain.search.dto.SearchRankResponseDTO;
import com.ceos.beatbuddy.domain.search.exception.SearchErrorCode;
import com.ceos.beatbuddy.domain.search.repository.SearchRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueGenreErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueMoodErrorCode;
import com.ceos.beatbuddy.domain.venue.repository.VenueGenreRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueMoodRepository;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;
    private final VenueGenreRepository venueGenreRepository;
    private final VenueMoodRepository venueMoodRepository;
    private final HeartbeatRepository heartbeatRepository;

    @Transactional
    public List<SearchQueryResponseDTO> keywordSearch(SearchDTO.RequestDTO searchRequestDTO, Long memberId) {

        List<SearchQueryResponseDTO> venueList= searchRepository.keywordFilter(searchRequestDTO, memberId);
        List<String> keywords = searchRequestDTO.getKeyword();

        for(String keyword: keywords){
            Double score =0.0;
            try {
                // 검색을하면 해당검색어를 value에 저장하고, score를 1 준다
                score = redisTemplate.opsForZSet().incrementScore("ranking", keyword,1.0);

                // 만료 시간 설정 (현재 시간 + 24시간)
                long expireAt = Instant.now().getEpochSecond() + 86400;
                Double expireAtDouble = Double.valueOf(expireAt);

                // 만료 시간을 Sorted Set에 저장
                redisTemplate.opsForZSet().add("expire", keyword, expireAtDouble);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
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

    public List<SearchQueryResponseDTO> searchDropDown(Long memberId, SearchDropDownDTO searchDropDownDTO) {

        final int genreIndex;
        final Region region;

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        String genreTag = searchDropDownDTO.getGenreTag();
        String regionTag = searchDropDownDTO.getRegionTag();
        List<String> keyword = searchDropDownDTO.getKeyword();

        if(keyword.isEmpty()) throw new CustomException(SearchErrorCode.KEYWORD_IS_EMPTY);
        if (genreTag.isEmpty() && regionTag.isEmpty()) throw new CustomException(VectorErrorCode.TAGS_EMPTY);

        if (!genreTag.isEmpty()) {
            int index = Vector.getGenreIndex(genreTag);
            if (index == -1) throw new CustomException(VectorErrorCode.GENRE_INDEX_NOT_EXIST);
            genreIndex = index;
        } else {
            genreIndex = -1;
        }

        if (!regionTag.isEmpty()) {
            Region tempRegion = Region.fromText(regionTag);
            if (tempRegion == null) throw new CustomException(MemberErrorCode.REGION_NOT_EXIST);
            region = tempRegion;
        } else {
            region = null;
        }

        SearchDTO.RequestDTO searchRequestDTO = SearchDTO.RequestDTO.builder().keyword(keyword).build();
        List<SearchQueryResponseDTO> venueList = searchRepository.keywordFilter(searchRequestDTO, memberId);


        List<SearchQueryResponseDTO> filteredList = new ArrayList<>(venueList);

        if (genreIndex != -1) {
            filteredList = filteredList.stream()
                    .map(v -> venueGenreRepository.findByVenueId(v.getVenueId())
                            .orElseThrow(() -> new CustomException(VenueGenreErrorCode.VENUE_GENRE_NOT_EXIST)))
                    .filter(vg -> (vg.getGenreVector().getElements().get(genreIndex) == 1.0))
                    .map(vg -> {
                        Venue venue = vg.getVenue();
                        VenueMood venueMood = venueMoodRepository.findByVenue(venue).orElseThrow(()->new CustomException(VenueMoodErrorCode.VENUE_MOOD_NOT_EXIST));
                        boolean isHeartbeat = heartbeatRepository.findByMemberVenue(member,venue).isPresent();

                        List<String> trueGenreElements = Vector.getTrueGenreElements(vg.getGenreVector());
                        List<String> trueMoodElements = Vector.getTrueMoodElements(venueMood.getMoodVector());

                        List<String> tagList = new ArrayList<>(trueGenreElements);
                        tagList.addAll(trueMoodElements);
                        tagList.add(venue.getRegion().getText());
                        return new SearchQueryResponseDTO(
                                LocalDateTime.now(),
                                venue.getVenueId(),
                                venue.getEnglishName(),
                                venue.getKoreanName(),
                                tagList,
                                venue.getHeartbeatNum(),
                                isHeartbeat,
                                venue.getLogoUrl(),
                                venue.getBackgroundUrl(),
                                venue.getAddress()
                        );
                    })
                    .collect(Collectors.toList());
        }

        if (region != null) {
            filteredList = filteredList.stream()
                    .filter(v -> {
                Venue venue = venueRepository.findById(v.getVenueId())
                        .orElseThrow(() -> new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
                return venue.getRegion() == region;
            })
                    .collect(Collectors.toList());
        }

        return filteredList;
    }

}
