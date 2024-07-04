package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VenueRepository extends JpaRepository<Venue, Long> {
    Long findByKoreanName(String koreanName);

    List<Venue> findByRegion(Region region);
}
