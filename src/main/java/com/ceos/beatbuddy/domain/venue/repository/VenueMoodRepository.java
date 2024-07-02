package com.ceos.beatbuddy.domain.venue.repository;


import com.ceos.beatbuddy.domain.venue.entity.VenueMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VenueMoodRepository extends JpaRepository<VenueMood, Long> {

    @Query("SELECT vm FROM VenueMood vm JOIN FETCH vm.venue")
    List<VenueMood> findAllWithVenue();
}
