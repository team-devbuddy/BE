package com.ceos.beatbuddy.domain.search.repository;

import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.search.dto.SearchQueryResponseDTO;
import com.ceos.beatbuddy.domain.search.exception.SearchErrorCode;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.entity.*;
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
        KOREAN_TO_ENGLISH_GENRES.put("힙합", "HIPHOP_R&B");
        KOREAN_TO_ENGLISH_GENRES.put("알앤비", "HIPHOP_R&B");
        KOREAN_TO_ENGLISH_GENRES.put("하우스", "HOUSE");
        KOREAN_TO_ENGLISH_GENRES.put("소울앤펑크", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("소울", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("펑크", "SOUL&FUNK");
        KOREAN_TO_ENGLISH_GENRES.put("테크노", "TECHNO");
        KOREAN_TO_ENGLISH_GENRES.put("케이팝", "K-POP");
        KOREAN_TO_ENGLISH_GENRES.put("팝", "POP");
        KOREAN_TO_ENGLISH_GENRES.put("라틴", "LATIN");

        KOREAN_TO_ENGLISH_MOODS.put("힙", "HIP");
        KOREAN_TO_ENGLISH_MOODS.put("힙한", "HIP");
        KOREAN_TO_ENGLISH_MOODS.put("다크", "DARK");
        KOREAN_TO_ENGLISH_MOODS.put("다크한", "DARK");
        KOREAN_TO_ENGLISH_MOODS.put("어두운", "DARK");
        KOREAN_TO_ENGLISH_MOODS.put("신나는", "EXCITING");
        KOREAN_TO_ENGLISH_MOODS.put("흥분되는", "EXCITING");
        KOREAN_TO_ENGLISH_MOODS.put("신이 나는", "EXCITING");
        KOREAN_TO_ENGLISH_MOODS.put("펑키", "FUNKY");
        KOREAN_TO_ENGLISH_MOODS.put("펑키한", "FUNKY");
        KOREAN_TO_ENGLISH_MOODS.put("이국적인", "EXOTIC");
        KOREAN_TO_ENGLISH_MOODS.put("트렌디", "TRENDY");
        KOREAN_TO_ENGLISH_MOODS.put("트렌디한", "TRENDY");
        KOREAN_TO_ENGLISH_MOODS.put("트로피컬", "TROPICAL");
        KOREAN_TO_ENGLISH_MOODS.put("트로피컬한", "TROPICAL");
        KOREAN_TO_ENGLISH_MOODS.put("칠한", "CHILLY");
        KOREAN_TO_ENGLISH_MOODS.put("칠", "CHILLY");
        KOREAN_TO_ENGLISH_MOODS.put("칠리", "CHILLY");
        KOREAN_TO_ENGLISH_MOODS.put("칠리한", "CHILLY");

        KOREAN_TO_ENGLISH_REGIONS.put("홍대", "HONGDAE");
        KOREAN_TO_ENGLISH_REGIONS.put("이태원", "ITAEWON");
        KOREAN_TO_ENGLISH_REGIONS.put("압구정", "APGUJEONG");
        KOREAN_TO_ENGLISH_REGIONS.put("신사", "SINSA");
    }


    public SearchRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    public List<SearchQueryResponseDTO> keywordFilter(SearchDTO.RequestDTO searchRequestDto) {

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
                            heartbeatNum);
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
