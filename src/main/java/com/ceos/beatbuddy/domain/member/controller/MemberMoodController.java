package com.ceos.beatbuddy.domain.member.controller;


import com.ceos.beatbuddy.domain.member.application.MemberGenreService;
import com.ceos.beatbuddy.domain.member.application.MemberMoodService;
import com.ceos.beatbuddy.domain.member.dto.MemberVectorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @DeleteMapping("/{memberId}/{memberMoodId}")
    public ResponseEntity<MemberVectorResponseDTO> deleteMoodPreference(@PathVariable Long memberId, @PathVariable Long memberMoodId) {
        return ResponseEntity.ok(memberMoodService.deleteMoodVector(memberId, memberMoodId));
    }

    @GetMapping("/all/{memberId}")
    public ResponseEntity<List<MemberVectorResponseDTO>> getAllMoodPreference(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberMoodService.getAllMoodVector(memberId));
    }

    @GetMapping("/latest/{memberId}")
    public ResponseEntity<MemberVectorResponseDTO> getLatestMoodPreference(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberMoodService.getLatestMoodVector(memberId));
    }
}
