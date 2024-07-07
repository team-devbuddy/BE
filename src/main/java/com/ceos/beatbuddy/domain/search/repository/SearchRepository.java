package com.ceos.beatbuddy.domain.search.repository;

import com.ceos.beatbuddy.domain.search.dto.SearchQueryDTO;
import com.ceos.beatbuddy.domain.venue.entity.QVenue;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManager;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.ceos.beatbuddy.domain.search.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;




import java.util.List;

@Repository
@Transactional
public class SearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public SearchRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Page<SearchQueryDTO> keywordFilter(SearchDTO.RequestDTO searchRequestDto, Pageable pageable) {

        QVenue venue = QVenue.venue;

        List<SearchQueryDTO> result = queryFactory
                .select(Projections.constructor(SearchQueryDTO.class, venue.venueId, venue.englishName, venue.koreanName, venue.address, venue.description))
                .from(venue)
                .where(
                        searchKeywordFilter(searchRequestDto.getKeyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(venue.count())
                .from(venue)
                .where(
                        searchKeywordFilter(searchRequestDto.getKeyword())
                );

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private BooleanExpression searchKeywordFilter(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        QVenue venue = QVenue.venue;
        BooleanExpression keywordFilter = null;

        for (String keyword : keywords) {
            BooleanExpression filter = venue.englishName.containsIgnoreCase(keyword)
                    .or(venue.koreanName.containsIgnoreCase(keyword))
                    .or(venue.address.containsIgnoreCase(keyword))
                    .or(venue.description.containsIgnoreCase(keyword));

            keywordFilter = keywordFilter == null ? filter : keywordFilter.and(filter);
        }

        return keywordFilter;
    }

//    private BooleanExpression tagContain(String keyword) {
//        return keyword != null ? Expressions.allOf(post.tags.any().tagName.contains(keyword)) : null;
//    }

    private BooleanExpression isFilterSearch(String keyword) {
        QVenue venue = QVenue.venue;

        return venue.englishName.containsIgnoreCase(keyword)
                .or(venue.koreanName.containsIgnoreCase(keyword))
                .or(venue.address.containsIgnoreCase(keyword));
    }
}
