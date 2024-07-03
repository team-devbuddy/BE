package com.ceos.beatbuddy.domain.venue.controller;


import com.ceos.beatbuddy.domain.venue.application.VenueGenreService;
import com.ceos.beatbuddy.domain.venue.application.VenueMoodService;
import com.ceos.beatbuddy.domain.venue.dto.VenueVectorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/venue-mood")
@RequiredArgsConstructor
public class VenueMoodController {

    private final VenueMoodService venueMoodService;

    @PostMapping("/{venueId}")
    public ResponseEntity<VenueVectorResponseDTO> addGenrePreference(@PathVariable Long venueId, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(venueMoodService.addMoodVector(venueId, preferences));
    }

    @PatchMapping("/{venueId}")
    public ResponseEntity<VenueVectorResponseDTO> updateGenrePreference(@PathVariable Long venueId, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(venueMoodService.updateMoodVector(venueId, preferences));
    }
}
