package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface VenueRepository extends JpaRepository<Venue, Long> {
    Long findByKoreanName(String koreanName);

    Optional<Venue> findByVenueId(Long venueId);
}

