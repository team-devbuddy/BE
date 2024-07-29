package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.entity.Venue;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueGenreRepository extends JpaRepository<VenueGenre, Long> {

    @Query("SELECT vg FROM VenueGenre vg JOIN FETCH vg.venue v WHERE v.region IN :regions")
    List<VenueGenre> findByVenueRegion(@Param("regions") List<Region> regions);

    @Query("SELECT vg FROM VenueGenre vg JOIN vg.venue v WHERE vg.venue = :venue")
    Optional<VenueGenre> findByVenue(@Param("venue")Venue venue);

    @Query("SELECT vg FROM VenueGenre vg WHERE vg.venue.venueId = :venueId")
    Optional<VenueGenre> findByVenueId(@Param("venueId") Long venueId);
}
