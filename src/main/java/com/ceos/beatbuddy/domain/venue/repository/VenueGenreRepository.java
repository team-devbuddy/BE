package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.venue.entity.VenueGenre;
import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VenueGenreRepository extends JpaRepository<VenueGenre, Long> {

    @Query("SELECT vg FROM VenueGenre vg JOIN FETCH vg.venue v WHERE v.region = :region")
    List<VenueGenre> findByVenueRegion(@Param("region") Region region);
}
