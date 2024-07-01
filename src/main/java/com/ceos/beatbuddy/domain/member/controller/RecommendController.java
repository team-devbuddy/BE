package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.RecommendService;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/genre/{memberId}")
    public ResponseEntity<List<VenueResponseDTO>> recommendByGenre(@PathVariable final Long memberId) {
        return ResponseEntity.ok(recommendService.recommendVenuesByGenre(memberId, 2L));
    }

}
