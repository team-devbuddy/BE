package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.venue.entity.Venue;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface VenueRepository extends JpaRepository<Venue, Long> {
    @Query("SELECT v.venueId FROM Venue v WHERE v.koreanName = :koreanName")
    Long findVenueIdByKoreanName(@Param("koreanName") String koreanName);

    @Query("SELECT v.venueId FROM Venue v")
    List<Long> findAllIds();

    @Query("SELECT v FROM Venue v ORDER BY v.heartbeatNum DESC LIMIT 10")
    List<Venue> sortByHeartbeatCount();

    @Query("SELECT v FROM Venue v WHERE v.venueId = :venueId")
    Long deleteByVenueId(@Param("venueId")Long venueId);

}

