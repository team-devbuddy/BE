package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.application.MemberMoodService;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/member-mood")
@RequiredArgsConstructor
public class MemberMoodController {
    private final MemberMoodService memberMoodService;

    @PostMapping("/{memberId}")
    public ResponseEntity<MemberVectorResponseDTO> addMoodPreference(@PathVariable Long memberId, @RequestBody Map<String, Double> preferences) {
        return ResponseEntity.ok(memberMoodService.addMoodVector(memberId, preferences));
    }
}
