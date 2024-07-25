package com.ceos.beatbuddy.domain.search.repository;

import com.ceos.beatbuddy.domain.heartbeat.repository.HeartbeatRepository;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryResponseDTO;
import com.ceos.beatbuddy.domain.search.exception.SearchErrorCode;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.entity.*;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.repository.VenueRepository;
import com.ceos.beatbuddy.global.CustomException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.persistence.EntityManager;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class SearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private static final Map<String, String> KOREAN_TO_ENGLISH_GENRES = new HashMap<>();
    private static final Map<String, String> KOREAN_TO_ENGLISH_MOODS = new HashMap<>();
    private static final Map<String, String> KOREAN_TO_ENGLISH_REGIONS = new HashMap<>();

    static {
        KOREAN_TO_ENGLISH_GENRES.put("이디엠", "EDM");
        KOREAN_TO_ENGLISH_GENRES.put("힙합", "HIPHOP");
        KOREAN_TO_ENGLISH_GENRES.put("알앤비", "R&B");
        KOREAN_TO_ENGLISH_GENRES.put("하우스", "HOUSE");
        KOREAN_TO_ENGLISH_GENRES.put("소울앤펑크", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("소울", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("펑크", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("테크노", "TECHNO");
        KOREAN_TO_ENGLISH_GENRES.put("케이팝", "K-POP");
        KOREAN_TO_ENGLISH_GENRES.put("팝", "POP");
        KOREAN_TO_ENGLISH_GENRES.put("라틴", "LATIN");
        KOREAN_TO_ENGLISH_GENRES.put("락", "ROCK");

        KOREAN_TO_ENGLISH_MOODS.put("펍", "PUB");
        KOREAN_TO_ENGLISH_MOODS.put("펍 느낌", "PUB");
        KOREAN_TO_ENGLISH_MOODS.put("펍라운지", "PUB");
        KOREAN_TO_ENGLISH_MOODS.put("펍 라운지", "PUB");
        KOREAN_TO_ENGLISH_MOODS.put("클럽", "CLUB");

        KOREAN_TO_ENGLISH_MOODS.put("딥", "DEEP");
        KOREAN_TO_ENGLISH_MOODS.put("딥한", "DEEP");
        KOREAN_TO_ENGLISH_MOODS.put("마이너한", "DEEP");
        KOREAN_TO_ENGLISH_MOODS.put("마이너", "DEEP");
        KOREAN_TO_ENGLISH_MOODS.put("밝은", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("라이트", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("라이트한", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("신나는", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("유쾌한", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("유쾌", "LIGHT");
        KOREAN_TO_ENGLISH_MOODS.put("이국적인", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("이국적", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("이색적인", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("이색적", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("새로운", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("이국", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("칠한", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("칠", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("칠리", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("칠리한", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("살랑살랑", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("살랑살랑한", "CHILL");
        KOREAN_TO_ENGLISH_MOODS.put("헌팅", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("만남", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("만남 친화적", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("만남친화적", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("사교적인", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("사교적", "HUNTING");
        KOREAN_TO_ENGLISH_MOODS.put("루프탑", "ROOFTOP");
        KOREAN_TO_ENGLISH_MOODS.put("옥상", "ROOFTOP");
        KOREAN_TO_ENGLISH_MOODS.put("옥탑", "ROOFTOP");


        KOREAN_TO_ENGLISH_REGIONS.put("홍대", "HONGDAE");
        KOREAN_TO_ENGLISH_REGIONS.put("이태원", "ITAEWON");
        KOREAN_TO_ENGLISH_REGIONS.put("압구정", "APGUJEONG");
        KOREAN_TO_ENGLISH_REGIONS.put("신사", "GANGNAM/SINSA");
        KOREAN_TO_ENGLISH_REGIONS.put("강남", "GANGNAM/SINSA");
        KOREAN_TO_ENGLISH_REGIONS.put("강남역", "GANGNAM/SINSA");
        KOREAN_TO_ENGLISH_REGIONS.put("신사역", "GANGNAM/SINSA");
        KOREAN_TO_ENGLISH_REGIONS.put("강남/신사", "GANGNAM/SINSA");
        KOREAN_TO_ENGLISH_REGIONS.put("신사/강남", "GANGNAM/SINSA");
    }

    private final HeartbeatRepository heartbeatRepository;
    private final MemberRepository memberRepository;
    private final VenueRepository venueRepository;


    public SearchRepository(EntityManager em, HeartbeatRepository heartbeatRepository, MemberRepository memberRepository, VenueRepository venueRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.heartbeatRepository = heartbeatRepository;
        this.memberRepository = memberRepository;
        this.venueRepository = venueRepository;
    }


    public List<SearchQueryResponseDTO> keywordFilter(SearchDTO.RequestDTO searchRequestDto, Long memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        QVenue venue = QVenue.venue;
        QVenueGenre venueGenre = QVenueGenre.venueGenre;
        QVenueMood venueMood = QVenueMood.venueMood;

        // Venue, VenueGenre, VenueMood를 조인하고 필요한 데이터를 선택합니다.
        List<SearchQueryResponseDTO> result = queryFactory
                .select(Projections.constructor(SearchDTO.mediumDTO.class,
                        Expressions.constant(LocalDateTime.now()),
                        venue.venueId,
                        venue.englishName,
                        venue.koreanName,
                        venue.heartbeatNum,
                        venueGenre.genreVectorString,
                        venueMood.moodVectorString,
                        venue.region))
                .from(venue)
                .leftJoin(venueGenre).on(venueGenre.venue.eq(venue))
                .leftJoin(venueMood).on(venueMood.venue.eq(venue))
                .where(searchKeywordFilter(searchRequestDto.getKeyword()))
                .fetch()
                .stream()
                .map(component -> {
                    LocalDateTime currentDate = component.getCurrentDate();
                    Long venueId = component.getVenueId();
                    String englishName = component.getEnglishName();
                    String koreanName = component.getKoreanName();
                    Long heartbeatNum = component.getHeartbeatNum();
                    String genreVector = component.getVenueGenre();
                    String moodVector = component.getVenueMood();
                    String region = component.getRegion().getText();
                    Venue componentVenue = venueRepository.findById(venueId).orElseThrow(()->new CustomException(VenueErrorCode.VENUE_NOT_EXIST));
                    String logoUrl = componentVenue.getLogoUrl();
                    boolean isHeartbeat = heartbeatRepository.findByMemberVenue(member,componentVenue).isPresent();

                    List<String> trueGenreElements = Vector.getTrueGenreElements(Vector.fromString(genreVector));
                    List<String> trueMoodElements = Vector.getTrueMoodElements(Vector.fromString(moodVector));

                    List<String> tagList = new ArrayList<>(trueGenreElements);
                    tagList.addAll(trueMoodElements);
                    tagList.add(region);

                    return new SearchQueryResponseDTO(
                            currentDate,
                            venueId,
                            englishName,
                            koreanName,
                            tagList,
                            heartbeatNum,
                            isHeartbeat,
                            logoUrl);
                })
                .collect(Collectors.toList());


        return result;
    }

    private BooleanExpression searchKeywordFilter(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            throw new CustomException(SearchErrorCode.KEYWORD_IS_EMPTY);
        }

        QVenue venue = QVenue.venue;
        QVenueGenre venueGenre = QVenueGenre.venueGenre;
        QVenueMood venueMood = QVenueMood.venueMood;

        BooleanExpression baseFilter = null;
        String keywordEnglish = null;

        for (String keyword : keywords) {
            Optional<String> keywordCheck = convertToEnglish(keyword);

            if (keywordCheck.isPresent()) {
                keywordEnglish = keywordCheck.get();
            } else keywordEnglish = keyword;

            BooleanExpression keywordFilter = venue.englishName.containsIgnoreCase(keyword)
                    .or(venue.koreanName.containsIgnoreCase(keyword))
                    .or(venue.address.containsIgnoreCase(keyword))
                    .or(venue.description.containsIgnoreCase(keyword));

            for (Region region : Region.values()) {
                if (region.getText().equalsIgnoreCase(keywordEnglish)) {
                    keywordFilter = keywordFilter.or(venue.region.eq(region));
                }
            }

            BooleanExpression genreFilter = Expressions.booleanTemplate("false");
            BooleanExpression moodFilter = Expressions.booleanTemplate("false");

            // Fetch genre and mood vectors from database and create filters
            List<Tuple> genreMoodTuples = queryFactory
                    .select(venue.venueId, venueGenre.genreVectorString, venueMood.moodVectorString)
                    .from(venue)
                    .leftJoin(venueGenre).on(venueGenre.venue.eq(venue))
                    .leftJoin(venueMood).on(venueMood.venue.eq(venue))
                    .where(venue.eq(venue))  // Apply any necessary conditions here
                    .fetch();

            for (Tuple tuple : genreMoodTuples) {
                String genreVectorString = tuple.get(venueGenre.genreVectorString);
                String moodVectorString = tuple.get(venueMood.moodVectorString);

                List<String> trueGenreElements = Vector.getTrueGenreElements(Vector.fromString(genreVectorString));
                List<String> trueMoodElements = Vector.getTrueMoodElements(Vector.fromString(moodVectorString));

                for (String genre : trueGenreElements) {
                    if (genre.equalsIgnoreCase(keywordEnglish)) {
                        genreFilter = genreFilter.or(venue.venueId.eq(tuple.get(venue.venueId)));
                    }
                }

                for (String mood : trueMoodElements) {
                    if (mood.equalsIgnoreCase(keywordEnglish)) {
                        moodFilter = moodFilter.or(venue.venueId.eq(tuple.get(venue.venueId)));
                    }
                }
            }

            keywordFilter = keywordFilter.or(genreFilter).or(moodFilter);

            if (baseFilter == null) {
                baseFilter = keywordFilter;
            } else {
                baseFilter = baseFilter.and(keywordFilter);
            }
        }

        return baseFilter;
    }

    private BooleanExpression isFilterSearch(String keyword) {
        QVenue venue = QVenue.venue;

        return venue.englishName.containsIgnoreCase(keyword)
                .or(venue.koreanName.containsIgnoreCase(keyword))
                .or(venue.address.containsIgnoreCase(keyword));
    }

    private Optional<String> convertToEnglish(String koreanWord) {
        if (KOREAN_TO_ENGLISH_GENRES.containsKey(koreanWord)) {
            return Optional.of(KOREAN_TO_ENGLISH_GENRES.get(koreanWord));
        }
        if (KOREAN_TO_ENGLISH_MOODS.containsKey(koreanWord)) {
            return Optional.of(KOREAN_TO_ENGLISH_MOODS.get(koreanWord));
        }
        if (KOREAN_TO_ENGLISH_REGIONS.containsKey(koreanWord)) {
            return Optional.of(KOREAN_TO_ENGLISH_REGIONS.get(koreanWord));
        }
        return Optional.empty();
    }
}
