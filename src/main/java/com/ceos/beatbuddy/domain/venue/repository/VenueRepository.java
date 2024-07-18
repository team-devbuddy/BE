package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Query("SELECT v.venueId FROM Venue v")
    List<Long> findAllIds();

    @Query("SELECT v FROM Venue v ORDER BY v.heartbeatNum DESC LIMIT 10")
    List<Venue> sortByHeartbeatCount();

    @Query("SELECT v FROM Venue v WHERE v.region = :region")
    List<Venue> findByRegions(@Param("region") Region region);


}

