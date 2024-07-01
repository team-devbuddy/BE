package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VenueGenreRepository extends JpaRepository<VenueGenre, Long> {

    @Query("SELECT vg FROM VenueGenre vg JOIN FETCH vg.venue")
    List<VenueGenre> findAllWithVenue();
}
